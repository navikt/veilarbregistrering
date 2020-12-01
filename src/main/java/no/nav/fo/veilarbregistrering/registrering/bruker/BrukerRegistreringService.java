package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.PROFILERING_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportTags;


public class BrukerRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(BrukerRegistreringService.class);

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final RegistreringTilstandRepository registreringTilstandRepository;
    private final ProfileringService profileringService;
    private final ProfileringRepository profileringRepository;
    private final OppfolgingGateway oppfolgingGateway;
    private final BrukerTilstandService brukerTilstandService;

    public BrukerRegistreringService(BrukerRegistreringRepository brukerRegistreringRepository,
                                     ProfileringRepository profileringRepository,
                                     OppfolgingGateway oppfolgingGateway,
                                     ProfileringService profileringService,
                                     RegistreringTilstandRepository registreringTilstandRepository,
                                     BrukerTilstandService brukerTilstandService) {
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.profileringRepository = profileringRepository;
        this.oppfolgingGateway = oppfolgingGateway;
        this.profileringService = profileringService;
        this.registreringTilstandRepository = registreringTilstandRepository;
        this.brukerTilstandService = brukerTilstandService;
    }

    @Transactional
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker) {
        validerBrukerRegistrering(ordinaerBrukerRegistrering, bruker);

        OrdinaerBrukerRegistrering oppettetBrukerRegistrering = brukerRegistreringRepository.lagre(ordinaerBrukerRegistrering, bruker);

        Profilering profilering = profilerBrukerTilInnsatsgruppe(bruker.getGjeldendeFoedselsnummer(), oppettetBrukerRegistrering.getBesvarelse());
        profileringRepository.lagreProfilering(oppettetBrukerRegistrering.getId(), profilering);

        oppfolgingGateway.aktiverBruker(bruker.getGjeldendeFoedselsnummer(), profilering.getInnsatsgruppe());
        reportTags(PROFILERING_EVENT, profilering.getInnsatsgruppe());

        OrdinaerBrukerBesvarelseMetrikker.rapporterOrdinaerBesvarelse(ordinaerBrukerRegistrering, profilering);
        LOG.info("Brukerregistrering gjennomført med data {}, Profilering {}", oppettetBrukerRegistrering, profilering);

        RegistreringTilstand registreringTilstand = RegistreringTilstand.medStatus(Status.OVERFORT_ARENA, oppettetBrukerRegistrering.getId());
        registreringTilstandRepository.lagre(registreringTilstand);

        return oppettetBrukerRegistrering;
    }

    private void validerBrukerRegistrering(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker) {
        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker.getGjeldendeFoedselsnummer());

        if (brukersTilstand.isUnderOppfolging()) {
            throw new RuntimeException("Bruker allerede under oppfølging.");
        }

        if (brukersTilstand.ikkeErOrdinaerRegistrering()) {
            throw new RuntimeException(String.format("Brukeren kan ikke registreres ordinært fordi utledet registreringstype er %s.", brukersTilstand.getRegistreringstype()));
        }

        try {
            ValideringUtils.validerBrukerRegistrering(ordinaerBrukerRegistrering);
        } catch (RuntimeException e) {
            LOG.warn("Ugyldig innsendt registrering. Besvarelse: {} Stilling: {}", ordinaerBrukerRegistrering.getBesvarelse(), ordinaerBrukerRegistrering.getSisteStilling());
            OrdinaerBrukerRegistreringMetrikker.rapporterInvalidRegistrering(ordinaerBrukerRegistrering);
            throw e;
        }
    }

    private Profilering profilerBrukerTilInnsatsgruppe(Foedselsnummer fnr, Besvarelse besvarelse) {
        return profileringService.profilerBruker(
                fnr.alder(now()),
                fnr,
                besvarelse);
    }
}