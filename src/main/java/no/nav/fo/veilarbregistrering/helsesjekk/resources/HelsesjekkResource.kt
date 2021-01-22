package no.nav.fo.veilarbregistrering.helsesjekk.resources

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/internal")
@Produces("application/json")
class HelsesjekkResource {

    @GET
    @Path("/isAlive")
    fun isAlive() {
    }

    @GET
    @Path("/isReady")
    fun isReady() {
    }

}