package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "MeldekortResource")
interface MeldekortApi {
    @Operation(summary = "Henter meldekort for arbeidssøker.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Ok"),
        ApiResponse(responseCode = "401", description = "Unauthorized - bruker er ikke autorisert"),
        ApiResponse(responseCode = "403", description = "Forbidden - ingen tilgang"),
        ApiResponse(responseCode = "500", description = "Ukjent feil")
    )
    fun hentMeldekort(): List<MeldekortDto>

    @Operation(summary = "Henter siste meldekort for arbeidssøker.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Ok"),
        ApiResponse(responseCode = "401", description = "Unauthorized - bruker er ikke autorisert"),
        ApiResponse(responseCode = "403", description = "Forbidden - ingen tilgang"),
        ApiResponse(responseCode = "500", description = "Ukjent feil")
    )
    fun hentSisteMeldekort(): MeldekortDto
}
