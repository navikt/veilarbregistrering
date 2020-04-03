package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.amplitude.AmplitudeLogger;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Optional.ofNullable;


public class SykmeldtBrukerRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(SykmeldtBrukerRegistreringService.class);

    private final BrukerRegistreringService brukerRegistreringService;
    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final UnleashService unleashService;
    private final OppfolgingGateway oppfolgingGateway;

    public SykmeldtBrukerRegistreringService(
            BrukerRegistreringService brukerRegistreringService,
            BrukerRegistreringRepository brukerRegistreringRepository,
                                             OppfolgingGateway oppfolgingGateway,
                                             UnleashService unleashService
    ) {
        this.brukerRegistreringService = brukerRegistreringService;
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.unleashService = unleashService;
        this.oppfolgingGateway = oppfolgingGateway;
    }

    @Transactional
    public long registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering, Bruker bruker) {
        ofNullable(sykmeldtRegistrering.getBesvarelse())
                .orElseThrow(() -> new RuntimeException("Besvarelse for sykmeldt ugyldig."));

        BrukersTilstand brukersTilstand = brukerRegistreringService.hentBrukersTilstand(bruker.getFoedselsnummer());

        if (brukersTilstand.ikkeErSykemeldtRegistrering()) {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }

        oppfolgingGateway.settOppfolgingSykmeldt(bruker.getFoedselsnummer(), sykmeldtRegistrering.getBesvarelse());
        long id = brukerRegistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, bruker.getAktorId());
        LOG.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);

        if (unleashService.isEnabled("veilarbregistrering.amplitude.test")) {
            AmplitudeLogger.log(bruker.getAktorId());
        }

        return id;
    }
}
