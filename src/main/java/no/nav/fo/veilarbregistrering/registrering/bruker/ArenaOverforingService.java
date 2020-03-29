package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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
        Optional<RegistreringTilstand> muligRegistreringTilstand = brukerRegistreringRepository.finnNesteRegistreringForOverforing();
        if (!muligRegistreringTilstand.isPresent()) {
            LOG.info("Ingen registreringer klare (status = MOTTATT) for overføring");
            return;
        }

        RegistreringTilstand registreringTilstand = muligRegistreringTilstand.orElseThrow(IllegalStateException::new);
        long brukerRegistreringId = registreringTilstand.getBrukerRegistreringId();

        Bruker bruker = brukerRegistreringRepository.hentBrukerTilknyttet(brukerRegistreringId);
        Profilering profilering = profileringRepository.hentProfileringForId(brukerRegistreringId);

        LOG.info("Overfører registrering med tilstand: {}", registreringTilstand);
        Status status = overfoerRegistreringTilArena(bruker.getFoedselsnummer(), profilering.getInnsatsgruppe());

        RegistreringTilstand oppdatertRegistreringTilstand = registreringTilstand.oppdaterStatus(status);
        LOG.info("Ny tilstand: {}", oppdatertRegistreringTilstand);
        brukerRegistreringRepository.oppdater(oppdatertRegistreringTilstand);

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.hentBrukerregistreringForId(brukerRegistreringId);

        arbeidssokerRegistrertProducer.publiserArbeidssokerRegistrert(
                bruker.getAktorId(),
                ordinaerBrukerRegistrering.getBrukersSituasjon(),
                ordinaerBrukerRegistrering.getOpprettetDato());
    }

    Status overfoerRegistreringTilArena(Foedselsnummer foedselsnummer, Innsatsgruppe innsatsgruppe) {
        try {
            oppfolgingGateway.aktiverBruker(foedselsnummer, innsatsgruppe);

        } catch (WebApplicationException e) {
            Response response = e.getResponse();
            response.bufferEntity(); // Hvis vi bare skal lese èn gang, blir denne overflødig
            String json = response.readEntity(String.class);

            AktiverBrukerFeil aktiverBrukerFeil = JsonUtils.fromJson(json, AktiverBrukerFeil.class);
            LOG.warn("Aktivering av bruker i Arena feilet med arsak: {}", aktiverBrukerFeil.getType(), e);
            return map(aktiverBrukerFeil);

        } catch (RuntimeException e) {
            LOG.error("Aktivering av bruker i Arena feilet:", e);
            return Status.TEKNISK_FEIL;
        }

        return Status.ARENA_OK;
    }

    private static Status map(AktiverBrukerFeil aktiverBrukerFeil) {
        Status status;
        switch (aktiverBrukerFeil.getType()) {
            case BRUKER_ER_UKJENT : {
                status = Status.BRUKER_ER_UKJENT;
                break;
            }
            case BRUKER_KAN_IKKE_REAKTIVERES: {
                status = Status.BRUKER_KAN_IKKE_REAKTIVERES;
                break;
            }
            case BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET: {
                status = Status.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET;
                break;
            }
            case BRUKER_MANGLER_ARBEIDSTILLATELSE: {
                status = Status.BRUKER_MANGLER_ARBEIDSTILLATELSE;
                break;
            }
            default:
                LOG.error("Ukjent returverdi fra veilarboppfolging/Arena: " + aktiverBrukerFeil.getType());
                status = Status.TEKNISK_FEIL;
        }
        return status;
    }
}
