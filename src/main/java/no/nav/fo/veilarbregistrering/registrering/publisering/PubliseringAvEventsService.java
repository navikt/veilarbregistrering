package no.nav.fo.veilarbregistrering.registrering.publisering;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidling;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidlingRepository;
import no.nav.fo.veilarbregistrering.registrering.formidling.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.registrering.formidling.Status.OVERFORT_ARENA;

public class PubliseringAvEventsService {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvEventsService.class);

    private final ProfileringRepository profileringRepository;
    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final RegistreringFormidlingRepository registreringFormidlingRepository;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;
    private final ArbeidssokerProfilertProducer arbeidssokerProfilertProducer;
    private final PrometheusMetricsService prometheusMetricsService;

    public PubliseringAvEventsService(
            ProfileringRepository profileringRepository,
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            RegistreringFormidlingRepository registreringFormidlingRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer,
            PrometheusMetricsService prometheusMetricsService) {
        this.profileringRepository = profileringRepository;
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.registreringFormidlingRepository = registreringFormidlingRepository;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
        this.arbeidssokerProfilertProducer = arbeidssokerProfilertProducer;
        this.prometheusMetricsService = prometheusMetricsService;
    }

    @Transactional
    public void publiserEvents() {
        rapporterRegistreringStatusAntallForPublisering();
        Optional<RegistreringFormidling> muligRegistreringTilstand = Optional.ofNullable(registreringFormidlingRepository.finnNesteRegistreringTilstandMed(OVERFORT_ARENA));
        if (!muligRegistreringTilstand.isPresent()) {
            LOG.info("Ingen registreringer klare (status = OVERFORT_ARENA) for publisering");
            return;
        }

        RegistreringFormidling registreringFormidling = muligRegistreringTilstand.orElseThrow(IllegalStateException::new);
        long brukerRegistreringId = registreringFormidling.getBrukerRegistreringId();

        Bruker bruker = brukerRegistreringRepository.hentBrukerTilknyttet(brukerRegistreringId);
        Profilering profilering = profileringRepository.hentProfileringForId(brukerRegistreringId);
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.hentBrukerregistreringForId(brukerRegistreringId);

        RegistreringFormidling oppdatertRegistreringFormidling = registreringFormidling.oppdaterStatus(Status.PUBLISERT_KAFKA);
        registreringFormidlingRepository.oppdater(oppdatertRegistreringFormidling);
        LOG.info("Ny tilstand for registrering: {}", oppdatertRegistreringFormidling);

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
            Map<Status, Integer> antallPerStatus = registreringFormidlingRepository.hentAntallPerStatus();
            prometheusMetricsService.rapporterRegistreringStatusAntall(antallPerStatus);
        } catch (Exception e) {
            LOG.error("Feil ved rapportering av antall statuser", e);
        }
    }
}