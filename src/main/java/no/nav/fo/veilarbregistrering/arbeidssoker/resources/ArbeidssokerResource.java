package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
@Path("/arbeidssoker")
@Produces("application/json")
@Api(value = "ArbeidssokerResource")
public class ArbeidssokerResource {

    @GET
    @Path("/periode")
    @ApiOperation(value = "Henter alle perioder hvor bruker er registrert som arbeidssøker.")
    public List<ArbeidssokerperiodeDto> hentArbeidssokerperioder(
        @QueryParam("fraOgMed") LocalDate fraOgMed,
        @QueryParam("tilOgMed") LocalDate tilOgMed
    ) {
        throw new UnsupportedOperationException("GET /arbeidssoker/periode er ikke klar til bruk");
    }

}
