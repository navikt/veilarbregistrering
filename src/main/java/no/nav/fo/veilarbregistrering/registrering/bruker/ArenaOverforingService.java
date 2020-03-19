package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ArenaOverforingService {

    private static final Logger LOG = LoggerFactory.getLogger(ArenaOverforingService.class);

    private final ProfileringRepository profileringRepository;
    private final OppfolgingGateway oppfolgingGateway;

    public ArenaOverforingService(ProfileringRepository profileringRepository, OppfolgingGateway oppfolgingGateway) {
        this.profileringRepository = profileringRepository;
        this.oppfolgingGateway = oppfolgingGateway;
    }

    //TODO: Hente fnr fra aktørService eller lagre i databasen på vei ned?
    public Status overfoerRegistreringTilArena(Foedselsnummer foedselsnummer, long brukerRegistreringId) {
        Profilering profilering = profileringRepository.hentProfileringForId(brukerRegistreringId);

        try {
            oppfolgingGateway.aktiverBruker(foedselsnummer, profilering.getInnsatsgruppe());

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
