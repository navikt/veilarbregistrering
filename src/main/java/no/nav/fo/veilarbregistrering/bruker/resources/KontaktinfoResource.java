package no.nav.fo.veilarbregistrering.bruker.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
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
    private final UnleashService unleashService;

    public KontaktinfoResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            KontaktinfoService kontaktinfoService,
            UnleashService unleashService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.kontaktinfoService = kontaktinfoService;
        this.unleashService = unleashService;
    }

    @GET
    @Path("/kontaktinfo")
    @ApiOperation(value = "Henter kontaktinformasjon (telefonnummer) til bruker.")
    @ApiResponses({
            @ApiResponse(code = 403, message = "Ingen tilgang"),
            @ApiResponse(code = 404, message = "Ikke funnet"),
            @ApiResponse(code = 500, message = "Ukjent feil")
    })
    public KontaktinfoDto hentSisteArbeidsforhold() {
        final Bruker bruker = userService.hentBruker();

        if (!unleashService.isEnabled("veilarbregistrering.kontaktinfo")) {
            throw new Feil(FeilType.SERVICE_UNAVAILABLE, "Tjenesten er ikke skrudd på");
        }

        pepClient.sjekkLesetilgangTilBruker(map(bruker));

        Kontaktinfo kontaktinfo = kontaktinfoService.hentKontaktinfo(bruker);
        return KontaktinfoMapper.map(kontaktinfo);
    }

}
