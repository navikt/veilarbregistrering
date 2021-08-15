package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import static no.nav.fo.veilarbregistrering.metrics.Events.MANUELL_REAKTIVERING_EVENT;

public class InaktivBrukerService {

    private static final Logger LOG = LoggerFactory.getLogger(InaktivBrukerService.class);

    private final BrukerTilstandService brukerTilstandService;
    private final ReaktiveringRepository reaktiveringRepository;
    private final OppfolgingGateway oppfolgingGateway;
    private final PrometheusMetricsService prometheusMetricsService;

    public InaktivBrukerService(
            BrukerTilstandService brukerTilstandService,
            ReaktiveringRepository reaktiveringRepository,
            OppfolgingGateway oppfolgingGateway,
            PrometheusMetricsService prometheusMetricsService) {
        this.brukerTilstandService = brukerTilstandService;
        this.reaktiveringRepository = reaktiveringRepository;
        this.oppfolgingGateway = oppfolgingGateway;

        this.prometheusMetricsService = prometheusMetricsService;
    }

    @Transactional
    public void reaktiverBruker(Bruker bruker, boolean erVeileder) {
        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker);
        if (!brukersTilstand.kanReaktiveres()) {
            throw new KanIkkeReaktiveresException("Bruker kan ikke reaktiveres.");
        }

        reaktiveringRepository.lagreReaktiveringForBruker(bruker.getAktorId());
        oppfolgingGateway.reaktiverBruker(bruker.getGjeldendeFoedselsnummer());

        LOG.info("Reaktivering av bruker med aktørId : {}", bruker.getAktorId());

        if (erVeileder) {
            prometheusMetricsService.registrer(MANUELL_REAKTIVERING_EVENT);
        }

        AlderMetrikker.rapporterAlder(prometheusMetricsService, bruker.getGjeldendeFoedselsnummer());
    }
}