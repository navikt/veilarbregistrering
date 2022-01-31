package no.nav.fo.veilarbregistrering.bruker.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "KontaktinfoResource")
public interface KontaktinfoApi {

    @Operation(summary = "Henter kontaktinformasjon (telefonnummer) til bruker.")
    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Ingen tilgang"),
            @ApiResponse(responseCode = "404", description = "Ikke funnet"),
            @ApiResponse(responseCode = "500", description = "Ukjent feil")
    })
    KontaktinfoDto hentKontaktinfo();
}
