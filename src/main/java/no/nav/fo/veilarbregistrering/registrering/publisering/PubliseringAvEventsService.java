package no.nav.fo.veilarbregistrering.registrering.publisering;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static no.nav.fo.veilarbregistrering.registrering.bruker.Status.OVERFORT_ARENA;
import static no.nav.fo.veilarbregistrering.registrering.bruker.Status.PUBLISERT_KAFKA;

public class PubliseringAvEventsService {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvEventsService.class);

    private final ProfileringRepository profileringRepository;
    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final RegistreringTilstandRepository registreringTilstandRepository;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;
    private final ArbeidssokerProfilertProducer arbeidssokerProfilertProducer;

    public PubliseringAvEventsService(
            ProfileringRepository profileringRepository,
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            RegistreringTilstandRepository registreringTilstandRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer) {
        this.profileringRepository = profileringRepository;
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.registreringTilstandRepository = registreringTilstandRepository;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
        this.arbeidssokerProfilertProducer = arbeidssokerProfilertProducer;
    }

    @Transactional
    public void publiserEvents() {
        rapporterRegistreringStatusAntallForPublisering();
        Optional<RegistreringTilstand> muligRegistreringTilstand = registreringTilstandRepository.finnNesteRegistreringTilstandMed(OVERFORT_ARENA);
        if (!muligRegistreringTilstand.isPresent()) {
            LOG.info("Ingen registreringer klare (status = OVERFORT_ARENA) for publisering");
            return;
        }

        RegistreringTilstand registreringTilstand = muligRegistreringTilstand.orElseThrow(IllegalStateException::new);
        long brukerRegistreringId = registreringTilstand.getBrukerRegistreringId();

        Bruker bruker = brukerRegistreringRepository.hentBrukerTilknyttet(brukerRegistreringId);
        Profilering profilering = profileringRepository.hentProfileringForId(brukerRegistreringId);
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.hentBrukerregistreringForId(brukerRegistreringId);

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
            rapporterRegistreringStatusAntall(OVERFORT_ARENA);
            rapporterRegistreringStatusAntall(PUBLISERT_KAFKA);
        } catch (Exception e) {
            LOG.error("Feil ved rapportering av antall statuser", e);
        }
    }

    private void rapporterRegistreringStatusAntall(Status status) {
        int antall = registreringTilstandRepository.hentAntall(status);
        PubliseringMetrikker.rapporterRegistreringStatusAntall(status, antall);
    }
}