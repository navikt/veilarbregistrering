package no.nav.fo.veilarbregistrering.bruker.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo;
import no.nav.fo.veilarbregistrering.bruker.KontaktinfoService;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;

@Component
@Path("/person")
@Produces("application/json")
@Api(value = "KontaktinfoResource", description = "Tjeneste for henting av kontaktinfo fra Kontakt og reservasjonsregisteret.")
public class KontaktinfoResource {

    private final KontaktinfoService kontaktinfoService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;

    public KontaktinfoResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            KontaktinfoService kontaktinfoService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.kontaktinfoService = kontaktinfoService;
    }

    @GET
    @Path("/kontaktinfo")
    @ApiOperation(value = "Henter kontaktinformasjon (telefonnummer) til bruker.")
    @ApiResponses({
            @ApiResponse(code = 403, message = "Ingen tilgang"),
            @ApiResponse(code = 404, message = "Ikke funnet"),
            @ApiResponse(code = 500, message = "Ukjent feil")
    })
    public KontaktinfoDto hentKontaktinfo() {
        final Bruker bruker = userService.hentBruker();

        pepClient.sjekkLesetilgangTilBruker(map(bruker));

        Kontaktinfo kontaktinfo = kontaktinfoService.hentKontaktinfo(bruker);
        return KontaktinfoMapper.map(kontaktinfo);
    }

}
