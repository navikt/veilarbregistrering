package no.nav.fo.veilarbregistrering.arbeidssoker.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDate

@Tag(name = "ArbeidssokerResource")
interface ArbeidssokerApi {

    @Deprecated("Bruk POST med ident i body for å unngå fnr i logger")
    @Operation(summary = "Henter alle perioder hvor bruker er registrert som arbeidssøker.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Ok"),
        ApiResponse(responseCode = "400", description = "Ugyldig periode - fra og med dato må være før til dato"),
        ApiResponse(responseCode = "401", description = "Unauthorized - bruker er ikke autorisert"),
        ApiResponse(responseCode = "403", description = "Forbidden - ingen tilgang"),
        ApiResponse(responseCode = "500", description = "Ukjent feil")
    )
    fun hentArbeidssokerperioder(
        @Parameter(required = true, description = "Fødselsnummer") fnr: String,
        @Parameter(required = true, description = "Fra og med dato") fraOgMed: LocalDate,
        @Parameter(description = "Til og med dato") tilOgMed: LocalDate?
    ): ArbeidssokerperioderDto

    @Operation(summary = "Henter alle perioder hvor bruker er registrert som arbeidssøker.")
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "Ok"),
            ApiResponse(responseCode = "400", description = "Ugyldig periode - fra og med dato må være før til dato"),
            ApiResponse(responseCode = "401", description = "Unauthorized - bruker er ikke autorisert"),
            ApiResponse(responseCode = "403", description = "Forbidden - ingen tilgang"),
            ApiResponse(responseCode = "500", description = "Ukjent feil")
    )
    fun hentArbeidssokerperioder(
            @RequestBody(description = "Fødselsnummer") fnr: Fnr?,
            @Parameter(required = true, description = "Fra og med dato") fraOgMed: LocalDate,
            @Parameter(description = "Til og med dato") tilOgMed: LocalDate?
    ): ArbeidssokerperioderDto
}
