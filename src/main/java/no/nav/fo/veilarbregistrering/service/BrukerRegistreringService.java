package no.nav.fo.veilarbregistrering.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig.RegistreringFeature;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.utils.ReaktiveringUtils;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static java.lang.Boolean.TRUE;

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
    public BrukerRegistrering registrerBruker(BrukerRegistrering bruker, String fnr) {

        if (!registreringFeature.erAktiv()) {
            throw new RuntimeException("Tjenesten er togglet av.");
        }

        if (hentStartRegistreringStatus(fnr).isUnderOppfolging()) {
            throw new RuntimeException("Bruker allerede under oppfølging.");
        }

        return opprettBruker(fnr, bruker);
    }

    public StartRegistreringStatus hentStartRegistreringStatus(String fnr) {
        AktivStatus aktivStatus = oppfolgingClient.hentOppfolgingsstatus(fnr);

        boolean oppfyllerBetingelseOmArbeidserfaring = startRegistreringUtilsService.harJobbetSammenhengendeSeksAvTolvSisteManeder(
                () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                LocalDate.now()
        );
        boolean oppfyllerBetingelseOmInaktivitet = startRegistreringUtilsService.oppfyllerBetingelseOmInaktivitet(
                LocalDate.now(),
                aktivStatus.getInaktiveringDato()
        );

        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(aktivStatus.isAktiv())
                .setKreverReaktivering(ReaktiveringUtils.kreverReaktivering(aktivStatus))
                .setJobbetSeksAvTolvSisteManeder(oppfyllerBetingelseOmArbeidserfaring)
                .setRegistrertNavSisteToAr(!oppfyllerBetingelseOmInaktivitet);

        log.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private BrukerRegistrering opprettBruker(String fnr, BrukerRegistrering bruker) {
        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);
        BrukerRegistrering brukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);
        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(fnr), TRUE));
        log.info("Brukerregistrering gjennomført med data {}", brukerRegistrering);
        return brukerRegistrering;
    }

}
