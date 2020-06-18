package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.time.LocalDate;

import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;

@Component
@Path("/arbeidssoker")
@Produces("application/json")
@Api(value = "ArbeidssokerResource")
public class ArbeidssokerResource {

    private final ArbeidssokerService arbeidssokerService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;

    public ArbeidssokerResource(ArbeidssokerService arbeidssokerService, UserService userService, VeilarbAbacPepClient pepClient) {
        this.arbeidssokerService = arbeidssokerService;
        this.userService = userService;
        this.pepClient = pepClient;
    }

    @GET
    @Path("/perioder")
    @ApiOperation(value = "Henter alle perioder hvor bruker er registrert som arbeidssøker.")
    public ArbeidssokerperioderDto hentArbeidssokerperioder(
        @QueryParam("fraOgMed") LocalDate fraOgMed,
        @QueryParam("tilOgMed") LocalDate tilOgMed
    ) {
        Bruker bruker = userService.hentBruker();

        pepClient.sjekkLesetilgangTilBruker(map(bruker));

        throw new UnsupportedOperationException("GET /arbeidssoker/periode er ikke klar til bruk");
    }

}
