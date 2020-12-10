package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.WebApplicationException;

public class InaktivBrukerService {

    private static final Logger LOG = LoggerFactory.getLogger(InaktivBrukerService.class);

    private final BrukerTilstandService brukerTilstandService;
    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final OppfolgingGateway oppfolgingGateway;

    public InaktivBrukerService(
            BrukerTilstandService brukerTilstandService,
            BrukerRegistreringRepository brukerRegistreringRepository,
            OppfolgingGateway oppfolgingGateway) {
        this.brukerTilstandService = brukerTilstandService;
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.oppfolgingGateway = oppfolgingGateway;
    }

    @Transactional
    public void reaktiverBruker(Bruker bruker) {
        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker.getGjeldendeFoedselsnummer());
        if (!brukersTilstand.kanReaktiveres()) {
            throw new RuntimeException("Bruker kan ikke reaktiveres.");
        }

        brukerRegistreringRepository.lagreReaktiveringForBruker(bruker.getAktorId());
        AktiverBrukerResultat aktiverBrukerResultat = oppfolgingGateway.reaktiverBruker(bruker.getGjeldendeFoedselsnummer());

        if (aktiverBrukerResultat.erFeil()) {
            throw new WebApplicationException(JsonUtils.toJson(new AktiveringFeilResponse(aktiverBrukerResultat.feil().toString())));
        }

        LOG.info("Reaktivering av bruker med aktørId : {}", bruker.getAktorId());
    }
}