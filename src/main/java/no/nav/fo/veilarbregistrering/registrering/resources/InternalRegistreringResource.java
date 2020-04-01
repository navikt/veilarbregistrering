package no.nav.fo.veilarbregistrering.registrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
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
    private final UnleashService unleashService;
    public InternalRegistreringResource(
            BrukerRegistreringService brukerRegistreringService,
            UnleashService unleashService
    ) {
        this.brukerRegistreringService = brukerRegistreringService;
        this.unleashService = unleashService;
    }

    @PUT
    @Path("/status")
    @ApiOperation(value = "Oppdaterer status for en registrering")
    public void OppdaterStatus(RegistreringTilstandDto registreringTilstand) {

        if (internalOppgaveApiErAktivt()) {
            // TODO: Returnere objektet?
            brukerRegistreringService.oppdaterRegistreringTilstand(registreringTilstand);
        }
    }

    private boolean internalOppgaveApiErAktivt() {
        return unleashService.isEnabled("veilarbregistrering.api.internal");
    }

}
