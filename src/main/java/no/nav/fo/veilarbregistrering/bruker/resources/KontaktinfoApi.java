package no.nav.fo.veilarbregistrering.bruker.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "KontaktinfoResource")
public interface KontaktinfoApi {

    @ApiOperation(value = "Henter kontaktinformasjon (telefonnummer) til bruker.")
    @ApiResponses({
            @ApiResponse(code = 403, message = "Ingen tilgang"),
            @ApiResponse(code = 404, message = "Ikke funnet"),
            @ApiResponse(code = 500, message = "Ukjent feil")
    })
    KontaktinfoDto hentKontaktinfo();
}
