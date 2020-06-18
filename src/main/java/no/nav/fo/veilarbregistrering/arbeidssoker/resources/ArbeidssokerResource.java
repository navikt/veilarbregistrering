package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.time.LocalDate;
import java.util.List;

@Component
@Path("/arbeidssoker")
@Produces("application/json")
@Api(value = "ArbeidssokerResource")
public class ArbeidssokerResource {

    private final ArbeidssokerService arbeidssokerService;

    public ArbeidssokerResource(ArbeidssokerService arbeidssokerService) {
        this.arbeidssokerService = arbeidssokerService;
    }

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
