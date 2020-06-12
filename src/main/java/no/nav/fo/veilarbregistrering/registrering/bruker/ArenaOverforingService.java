package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.ArenaAktiveringException;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class ArenaOverforingService {

    private static final Logger LOG = LoggerFactory.getLogger(ArenaOverforingService.class);

    private final ProfileringRepository profileringRepository;
    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final OppfolgingGateway oppfolgingGateway;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;

    public ArenaOverforingService(ProfileringRepository profileringRepository, BrukerRegistreringRepository brukerRegistreringRepository, OppfolgingGateway oppfolgingGateway, ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer) {
        this.profileringRepository = profileringRepository;
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.oppfolgingGateway = oppfolgingGateway;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
    }

    /**
     * Stegene som skal gjøres:
     * 1) Hente neste registrering som er klar for overføring
     * - avbryt hvis det ikke er flere som er klare
     * 2) Hent grunnlaget for registreringen;
     * - fødselsnummer (fra registreringen
     * - innsatsgruppe (fra profileringen)
     * 3) Kalle Arena og tolke evt. feil i retur
     * 4) Oppdatere status på registreringen
     * 5) Publiser event på Kafka
     */
    @Transactional
    public void utforOverforing() {
        Optional<AktiveringTilstand> muligRegistreringTilstand = brukerRegistreringRepository.finnNesteAktiveringTilstandForOverforing();
        if (!muligRegistreringTilstand.isPresent()) {
            LOG.info("Ingen registreringer klare (status = MOTTATT) for overføring");
            return;
        }

        AktiveringTilstand aktiveringTilstand = muligRegistreringTilstand.orElseThrow(IllegalStateException::new);
        long brukerRegistreringId = aktiveringTilstand.getBrukerRegistreringId();

        Bruker bruker = brukerRegistreringRepository.hentBrukerTilknyttet(brukerRegistreringId);
        Profilering profilering = profileringRepository.hentProfileringForId(brukerRegistreringId);

        LOG.info("Overfører registrering med tilstand: {}", aktiveringTilstand);
        Status status;
        try {
            status = overfoerRegistreringTilArena(bruker.getFoedselsnummer(), profilering.getInnsatsgruppe());
        } catch (RuntimeException e) {
            LOG.error("Asynk overføring til Arena feilet av ukjent grunn", e);
            status = Status.UKJENT_TEKNISK_FEIL;
        }

        AktiveringTilstand oppdatertAktiveringTilstand = aktiveringTilstand.oppdaterStatus(status);
        LOG.info("Ny tilstand: {}", oppdatertAktiveringTilstand);
        brukerRegistreringRepository.oppdater(oppdatertAktiveringTilstand);

        if (!Status.ARENA_OK.equals(status)) {
            LOG.info("Avbryter og publiserer ingen Kafka-event pga. manglende OK fra Arena");
            return;
        }
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.hentBrukerregistreringForId(brukerRegistreringId);

        arbeidssokerRegistrertProducer.publiserArbeidssokerRegistrert(
                bruker.getAktorId(),
                ordinaerBrukerRegistrering.getBrukersSituasjon(),
                ordinaerBrukerRegistrering.getOpprettetDato());
    }

    Status overfoerRegistreringTilArena(Foedselsnummer foedselsnummer, Innsatsgruppe innsatsgruppe) {
        try {
            oppfolgingGateway.aktiverBruker(foedselsnummer, innsatsgruppe);

        } catch (ArenaAktiveringException e) {
            LOG.error("Aktivering av bruker i Arena feilet", e);
            return e.getStatus();

        } catch (RuntimeException e) {
            LOG.error("Aktivering av bruker i Arena feilet", e);
            return Status.TEKNISK_FEIL;
        }

        return Status.ARENA_OK;
    }
}
