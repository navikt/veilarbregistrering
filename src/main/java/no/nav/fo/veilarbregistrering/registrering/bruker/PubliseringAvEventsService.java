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
    public void utforPublisering() {
        Optional<AktiveringTilstand> muligRegistreringTilstand = aktiveringTilstandRepository.finnNesteAktiveringTilstandForOverforing();
        if (!muligRegistreringTilstand.isPresent()) {
            LOG.info("Ingen registreringer klare (status = MOTTATT) for overføring");
            return;
        }

        AktiveringTilstand aktiveringTilstand = muligRegistreringTilstand.orElseThrow(IllegalStateException::new);
        long brukerRegistreringId = aktiveringTilstand.getBrukerRegistreringId();

        Bruker bruker = brukerRegistreringRepository.hentBrukerTilknyttet(brukerRegistreringId);
        Profilering profilering = profileringRepository.hentProfileringForId(brukerRegistreringId);

        AktiveringTilstand oppdatertAktiveringTilstand = aktiveringTilstand.oppdaterStatus(Status.EVENT_PUBLISERT);
        LOG.info("Ny tilstand: {}", oppdatertAktiveringTilstand);
        aktiveringTilstandRepository.oppdater(oppdatertAktiveringTilstand);

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.hentBrukerregistreringForId(brukerRegistreringId);

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
}