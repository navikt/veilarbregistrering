package no.nav.fo.veilarbregistrering.service;

import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig.RegistreringFeature;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.utils.SelvgaaendeUtil.erSelvgaaende;

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

        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);

        boolean erSelvaaende = erSelvgaaende(bruker, status);

        if (!erSelvaaende) {
            throw new RuntimeException("Bruker oppfyller ikke krav for registrering.");
        }

        return opprettBruker(fnr, bruker, aktorId);
    }

    public StartRegistreringStatus hentStartRegistreringStatus(String fnr) {
        Optional<OppfolgingStatus> oppfolgingStatus = oppfolgingClient.hentOppfolgingsstatus(fnr);

        if (oppfolgingStatus.isPresent() && oppfolgingStatus.get().isUnderOppfolging()) {
            return new StartRegistreringStatus()
                    .setUnderOppfolging(true)
                    .setOppfyllerKravForAutomatiskRegistrering(false);
        }

        boolean oppfyllerKrav = startRegistreringUtilsService.oppfyllerKravOmAutomatiskRegistrering(
                fnr,
                () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                oppfolgingStatus.orElse(null), LocalDate.now()
        );

        return new StartRegistreringStatus()
                .setUnderOppfolging(false)
                .setOppfyllerKravForAutomatiskRegistrering(oppfyllerKrav);
    }

    private BrukerRegistrering opprettBruker(String fnr, BrukerRegistrering bruker, AktorId aktorId) {
        BrukerRegistrering brukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);

        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(fnr), "IKVAL"));
        return brukerRegistrering;
    }
}
