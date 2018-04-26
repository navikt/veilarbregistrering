package no.nav.fo.veilarbregistrering.resources;

import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/")
@Produces("application/json")
public class RegistreringResource {


    @GET
    @Path("/startregistrering")
    public StartRegistreringStatus hentStartRegistreringStatus() {
        return null;
    }

    @POST
    @Path("/startregistrering")
    public BrukerRegistrering registrerBruker(BrukerRegistrering brukerRegistrering) {
        return null;
    }

}
