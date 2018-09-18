package no.nav.fo.veilarbregistrering.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig.RegistreringFeature;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.service.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.utledAlderForFnr;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.rapporterInvalidRegistrering;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.rapporterProfilering;


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

        Boolean kanReaktiveres = hentStartRegistreringStatus(fnr).getKreverReaktivering();
        if (kanReaktiveres == null || !kanReaktiveres) {
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
            // throw new RuntimeException("Tjenesten er togglet av.");
        }

        StartRegistreringStatus startRegistreringStatus = hentStartRegistreringStatus(fnr);

        if (startRegistreringStatus.isUnderOppfolging()) {
            throw new RuntimeException("Bruker allerede under oppfølging.");
        }

        try {
            validerBrukerRegistrering(bruker);
        } catch (RuntimeException e) {
            log.warn("Ugyldig innsendt registrering. Besvarelse: {} Stilling: {}", bruker.getBesvarelse(), bruker.getSisteStilling());
            rapporterInvalidRegistrering(bruker);
            throw e;
        }

        Profilering profilering = profilerBrukerTilInnsatsgruppe(fnr, bruker);

        return opprettBruker(fnr, bruker, profilering);
    }

    public StartRegistreringStatus hentStartRegistreringStatus(String fnr) {
        OppfolgingStatusData oppfolgingStatusData = oppfolgingClient.hentOppfolgingsstatus(fnr);

        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(oppfolgingStatusData.isUnderOppfolging())
                .setKreverReaktivering(oppfolgingStatusData.getKanReaktiveres())
                .setErIkkeArbeidssokerUtenOppfolging(oppfolgingStatusData.getErIkkeArbeidssokerUtenOppfolging());
        
        if(!oppfolgingStatusData.isUnderOppfolging()) {
            boolean oppfyllerBetingelseOmArbeidserfaring = startRegistreringUtilsService.harJobbetSammenhengendeSeksAvTolvSisteManeder(
                    () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                    now());
            startRegistreringStatus.setJobbetSeksAvTolvSisteManeder(oppfyllerBetingelseOmArbeidserfaring);
        }

        log.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private BrukerRegistrering opprettBruker(String fnr, BrukerRegistrering bruker, Profilering profilering) {
        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);

        BrukerRegistrering brukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);
        arbeidssokerregistreringRepository.lagreProfilering(brukerRegistrering.getId(), profilering);
        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(fnr), profilering.getInnsatsgruppe()));

        rapporterProfilering(profilering);
        log.info("Brukerregistrering gjennomført med data {}, Profilering {}", brukerRegistrering, profilering);
        return brukerRegistrering;
    }

    public ProfilertBrukerRegistrering hentProfilertBrukerRegistrering(Fnr fnr) {
        return arbeidssokerregistreringRepository.hentProfilertBrukerregistreringForAktorId(
                FnrUtils.getAktorIdOrElseThrow(aktorService, fnr.getFnr())
        );
    }

    private Profilering profilerBrukerTilInnsatsgruppe(String fnr, BrukerRegistrering bruker) {
        return startRegistreringUtilsService.profilerBruker(
                bruker,
                utledAlderForFnr(fnr, now()),
                () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                now());
    }
}
