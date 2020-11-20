package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class PubliseringAvEventsService {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvEventsService.class);

    private final ProfileringRepository profileringRepository;
    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final AktiveringTilstandRepository aktiveringTilstandRepository;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;
    private final ArbeidssokerProfilertProducer arbeidssokerProfilertProducer;

    public PubliseringAvEventsService(
            ProfileringRepository profileringRepository,
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            AktiveringTilstandRepository aktiveringTilstandRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer) {
        this.profileringRepository = profileringRepository;
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.aktiveringTilstandRepository = aktiveringTilstandRepository;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
        this.arbeidssokerProfilertProducer = arbeidssokerProfilertProducer;
    }

    @Transactional
    public void publiserEvents() {
        Optional<AktiveringTilstand> muligRegistreringTilstand = aktiveringTilstandRepository.nesteRegistreringKlarForPublisering();
        if (!muligRegistreringTilstand.isPresent()) {
            LOG.info("Ingen registreringer klare (status = OVERFOERT_ARENA) for publisering");
            return;
        }

        AktiveringTilstand aktiveringTilstand = muligRegistreringTilstand.orElseThrow(IllegalStateException::new);
        long brukerRegistreringId = aktiveringTilstand.getBrukerRegistreringId();

        Bruker bruker = brukerRegistreringRepository.hentBrukerTilknyttet(brukerRegistreringId);
        Profilering profilering = profileringRepository.hentProfileringForId(brukerRegistreringId);
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.hentBrukerregistreringForId(brukerRegistreringId);

        /*
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
                ordinaerBrukerRegistrering.getOpprettetDato());*/

        AktiveringTilstand oppdatertAktiveringTilstand = aktiveringTilstand.oppdaterStatus(Status.EVENT_PUBLISERT);
        aktiveringTilstandRepository.oppdater(oppdatertAktiveringTilstand);
        LOG.info("Ny tilstand: {}", oppdatertAktiveringTilstand);
    }
}