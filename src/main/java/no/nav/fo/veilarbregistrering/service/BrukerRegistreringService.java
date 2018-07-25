package no.nav.fo.veilarbregistrering.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig.RegistreringFeature;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker;
import no.nav.fo.veilarbregistrering.utils.ReaktiveringUtils;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.utledAlderForFnr;


@Slf4j
public class BrukerRegistreringService {

    private final ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private final AktorService aktorService;
    private final RegistreringFeature registreringFeature;
    private OppfolgingClient oppfolgingClient;
    private ArbeidsforholdService arbeidsforholdService;
    private StartRegistreringUtilsService startRegistreringUtilsService;

    public BrukerRegistreringService(ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
                                     AktorService aktorService,
                                     RegistreringFeature registreringFeature,
                                     OppfolgingClient oppfolgingClient,
                                     ArbeidsforholdService arbeidsforholdService,
                                     StartRegistreringUtilsService startRegistreringUtilsService

    ) {
        this.arbeidssokerregistreringRepository = arbeidssokerregistreringRepository;
        this.aktorService = aktorService;
        this.registreringFeature = registreringFeature;
        this.oppfolgingClient = oppfolgingClient;
        this.arbeidsforholdService = arbeidsforholdService;
        this.startRegistreringUtilsService = startRegistreringUtilsService;
    }

    @Transactional
    public void reaktiverBruker(String fnr) {

        if (!registreringFeature.erAktiv()) {
            throw new RuntimeException("Tjenesten er togglet av.");
        }

        if (!hentStartRegistreringStatus(fnr).isKreverReaktivering()) {
            throw new RuntimeException("Bruker kan ikke reaktiveres.");
        }

        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);

        arbeidssokerregistreringRepository.lagreReaktiveringForBruker(aktorId);
        oppfolgingClient.reaktiverBruker(fnr);

        log.info("Reaktivering av bruker med aktørId : {}", aktorId);
    }

    @Transactional
    public BrukerRegistrering registrerBruker(BrukerRegistrering bruker, String fnr) {

        if (!registreringFeature.erAktiv()) {
            throw new RuntimeException("Tjenesten er togglet av.");
        }

        if (hentStartRegistreringStatus(fnr).isUnderOppfolging()) {
            throw new RuntimeException("Bruker allerede under oppfølging.");
        }

        startRegistreringUtilsService.validerBrukerRegistrering(bruker);

        Profilering profilering = profilerBrukerTilInnsatsgruppe(fnr, bruker);
        return opprettBruker(fnr, bruker, profilering);
    }

    public StartRegistreringStatus hentStartRegistreringStatus(String fnr) {
        AktivStatus aktivStatus = oppfolgingClient.hentOppfolgingsstatus(fnr);

        boolean oppfyllerBetingelseOmArbeidserfaring = startRegistreringUtilsService.harJobbetSammenhengendeSeksAvTolvSisteManeder(
                () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                now()
        );

        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(aktivStatus.isAktiv())
                .setKreverReaktivering(ReaktiveringUtils.kreverReaktivering(aktivStatus))
                .setJobbetSeksAvTolvSisteManeder(oppfyllerBetingelseOmArbeidserfaring);

        FunksjonelleMetrikker.rapporterRegistreringsstatus(aktivStatus, startRegistreringStatus);
        log.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private BrukerRegistrering opprettBruker(String fnr, BrukerRegistrering bruker, Profilering profilering) {
        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);

        BrukerRegistrering brukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);
        arbeidssokerregistreringRepository.lagreProfilering(brukerRegistrering.getId(), profilering);
        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(fnr), profilering.getInnsatsgruppe()));

        log.info("Brukerregistrering gjennomført med data {}, Profilering {}", brukerRegistrering, profilering);
        return brukerRegistrering;
    }

    private Profilering profilerBrukerTilInnsatsgruppe(String fnr, BrukerRegistrering bruker) {
        return startRegistreringUtilsService.profilerBruker(
                bruker,
                utledAlderForFnr(fnr, now()),
                () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                now());
    }

}
