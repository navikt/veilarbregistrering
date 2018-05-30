package no.nav.fo.veilarbregistrering.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig.RegistreringFeature;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static no.nav.fo.veilarbregistrering.utils.SelvgaaendeUtil.erSelvgaaende;

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

        StartRegistreringStatus status = hentStartRegistreringStatus(fnr);
        
        if (!erSelvgaaende(bruker, status)) {
            throw new RuntimeException("Bruker oppfyller ikke krav for registrering.");
        }

        return opprettBruker(fnr, bruker);
    }

    public StartRegistreringStatus hentStartRegistreringStatus(String fnr) {
        AktivStatus aktivStatus = oppfolgingClient.hentOppfolgingsstatus(fnr);

        StartRegistreringStatus startRegistreringStatus;
        if (aktivStatus.isAktiv()) {
            startRegistreringStatus = new StartRegistreringStatus()
                    .setUnderOppfolging(true)
                    .setOppfyllerKravForAutomatiskRegistrering(false);
        } else {
            boolean oppfyllerKrav = startRegistreringUtilsService.oppfyllerKravOmAutomatiskRegistrering(
                    fnr,
                    () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                    aktivStatus,
                    LocalDate.now()
            );
            startRegistreringStatus = new StartRegistreringStatus()
                    .setUnderOppfolging(false)
                    .setOppfyllerKravForAutomatiskRegistrering(oppfyllerKrav);
        }

        log.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private BrukerRegistrering opprettBruker(String fnr, BrukerRegistrering bruker) {
        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);
        BrukerRegistrering brukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);

        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(fnr), "IKVAL"));
        return brukerRegistrering;
    }
}
