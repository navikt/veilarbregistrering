package no.nav.fo.veilarbregistrering.registrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import org.springframework.stereotype.Component;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/internal")
@Produces("application/json")
@Api(value = "InternalRegistreringResource")
public class InternalRegistreringResource {

    private final BrukerRegistreringService brukerRegistreringService;

    public InternalRegistreringResource(
            BrukerRegistreringService brukerRegistreringService
    ) {
        this.brukerRegistreringService = brukerRegistreringService;
    }

    @PUT
    @Path("/status")
    @ApiOperation(value = "Oppdaterer status for en registrering")
    public void OppdaterStatus(RegistreringTilstandDto registreringTilstand) {

        // TODO: Returnere objektet?
        brukerRegistreringService.oppdaterRegistreringTilstand(registreringTilstand);
    }
}
