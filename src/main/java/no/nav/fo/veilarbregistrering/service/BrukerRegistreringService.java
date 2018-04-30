package no.nav.fo.veilarbregistrering.service;

import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig.OpprettBrukerIArenaFeature;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig.RegistreringFeature;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.utils.SelvgaaendeUtil.erSelvgaaende;
import static no.nav.fo.veilarbregistrering.utils.StartRegistreringUtils.erUnderoppfolgingIArena;
import static no.nav.fo.veilarbregistrering.utils.StartRegistreringUtils.oppfyllerKravOmAutomatiskRegistrering;

public class BrukerRegistreringService {

    private final ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private final AktorService aktorService;
    private final OpprettBrukerIArenaFeature opprettBrukerIArenaFeature;
    private final RegistreringFeature registreringFeature;
    private OppfolgingService oppfolgingService;
    private ArbeidsforholdService arbeidsforholdService;

    public BrukerRegistreringService(ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
                                     AktorService aktorService,
                                     OpprettBrukerIArenaFeature opprettBrukerIArenaFeature,
                                     RegistreringFeature registreringFeature,
                                     OppfolgingService oppfolgingService,
                                     ArbeidsforholdService arbeidsforholdService
    ) {
        this.arbeidssokerregistreringRepository = arbeidssokerregistreringRepository;
        this.aktorService = aktorService;
        this.opprettBrukerIArenaFeature = opprettBrukerIArenaFeature;
        this.registreringFeature = registreringFeature;
        this.oppfolgingService = oppfolgingService;
        this.arbeidsforholdService = arbeidsforholdService;
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
        Optional<OppfolgingStatus> oppfolgingStatus = hentOppfolgingsstatusOgFlagg(fnr);

        if(oppfolgingStatus.isPresent() && oppfolgingStatus.get().isOppfolgingsFlaggFO()) {
            return new StartRegistreringStatus().setUnderOppfolging(true).setOppfyllerKravForAutomatiskRegistrering(false);
        }

        boolean underOppfolgingIArena = oppfolgingStatus.isPresent() && erUnderoppfolgingIArena(oppfolgingStatus.get());

        if (underOppfolgingIArena) {
            return new StartRegistreringStatus()
                    .setUnderOppfolging(true)
                    .setOppfyllerKravForAutomatiskRegistrering(false);
        }

        boolean oppfyllerKrav = oppfyllerKravOmAutomatiskRegistrering(fnr, () -> arbeidsforholdService.hentArbeidsforhold(fnr), oppfolgingStatus.orElse(null), LocalDate.now());

        return new StartRegistreringStatus()
                .setUnderOppfolging(false)
                .setOppfyllerKravForAutomatiskRegistrering(oppfyllerKrav);
    }

    private BrukerRegistrering opprettBruker(String fnr, BrukerRegistrering bruker, AktorId aktorId) {
        BrukerRegistrering brukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);

        if (opprettBrukerIArenaFeature.erAktiv()) {
            arkiverBruker(new AktiverArbeidssokerData(new Fnr(fnr), "IKVAL"));
        }
        return brukerRegistrering;
    }

    private Optional<OppfolgingStatus> hentOppfolgingsstatusOgFlagg(String fnr) {
        return oppfolgingService.hentOppfolgingsstatusOgFlagg(fnr);
    }

    private void arkiverBruker(AktiverArbeidssokerData aktiverArbeidssokerData) {
        oppfolgingService.aktiverBruker(aktiverArbeidssokerData);
    }

}
