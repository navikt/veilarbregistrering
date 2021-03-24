package no.nav.fo.veilarbregistrering.registrering.publisering;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.registrering.tilstand.Status.OVERFORT_ARENA;

public class PubliseringAvEventsService {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvEventsService.class);

    private final ProfileringRepository profileringRepository;
    private final OrdinaerBrukerRegistreringRepository ordinaerBrukerRegistreringRepository;
    private final RegistreringTilstandRepository registreringTilstandRepository;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;
    private final ArbeidssokerProfilertProducer arbeidssokerProfilertProducer;
    private final PrometheusMetricsService prometheusMetricsService;

    public PubliseringAvEventsService(
            ProfileringRepository profileringRepository,
            OrdinaerBrukerRegistreringRepository ordinaerBrukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            RegistreringTilstandRepository registreringTilstandRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer,
            PrometheusMetricsService prometheusMetricsService) {
        this.profileringRepository = profileringRepository;
        this.ordinaerBrukerRegistreringRepository = ordinaerBrukerRegistreringRepository;
        this.registreringTilstandRepository = registreringTilstandRepository;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
        this.arbeidssokerProfilertProducer = arbeidssokerProfilertProducer;
        this.prometheusMetricsService = prometheusMetricsService;
    }

    @Transactional
    public void publiserEvents() {
        rapporterRegistreringStatusAntallForPublisering();
        Optional<RegistreringTilstand> muligRegistreringTilstand = Optional.ofNullable(registreringTilstandRepository.finnNesteRegistreringTilstandMed(OVERFORT_ARENA));
        if (!muligRegistreringTilstand.isPresent()) {
            LOG.info("Ingen registreringer klare (status = OVERFORT_ARENA) for publisering");
            return;
        }

        RegistreringTilstand registreringTilstand = muligRegistreringTilstand.orElseThrow(IllegalStateException::new);
        long brukerRegistreringId = registreringTilstand.getBrukerRegistreringId();

        Bruker bruker = ordinaerBrukerRegistreringRepository.hentBrukerTilknyttet(brukerRegistreringId);
        Profilering profilering = profileringRepository.hentProfileringForId(brukerRegistreringId);
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = ordinaerBrukerRegistreringRepository.hentBrukerregistreringForId(brukerRegistreringId);

        RegistreringTilstand oppdatertRegistreringTilstand = registreringTilstand.oppdaterStatus(Status.PUBLISERT_KAFKA);
        registreringTilstandRepository.oppdater(oppdatertRegistreringTilstand);
        LOG.info("Ny tilstand for registrering: {}", oppdatertRegistreringTilstand);

        // Det er viktig at publiserArbeidssokerRegistrert kjører før publiserProfilering fordi
        // førstnevnte sin producer håndterer at melding med samme id overskrives hvis den er publisert fra før.
        // Dette skjer pga. compaction-innstillingen definert i paw-iac repoet på github.
        // Så hvis førstnevnte feiler forhindrer vi at duplikate meldinger skrives til sistnevnte.

        arbeidssokerRegistrertProducer.publiserArbeidssokerRegistrert(
                new ArbeidssokerRegistrertInternalEvent(
                        bruker.getAktorId(),
                        ordinaerBrukerRegistrering.getBesvarelse(),
                        ordinaerBrukerRegistrering.getOpprettetDato()));

        arbeidssokerProfilertProducer.publiserProfilering(
                bruker.getAktorId(),
                profilering.getInnsatsgruppe(),
                ordinaerBrukerRegistrering.getOpprettetDato());

    }

    private void rapporterRegistreringStatusAntallForPublisering() {
        try {
            Map<Status, Integer> antallPerStatus = registreringTilstandRepository.hentAntallPerStatus();
            prometheusMetricsService.rapporterRegistreringStatusAntall(antallPerStatus);
        } catch (Exception e) {
            LOG.error("Feil ved rapportering av antall statuser", e);
        }
    }
}