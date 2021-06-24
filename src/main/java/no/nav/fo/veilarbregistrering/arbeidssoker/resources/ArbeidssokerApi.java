package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;

@Tag(name = "ArbeidssokerResource")
public interface ArbeidssokerApi {

    @Operation(summary = "Henter alle perioder hvor bruker er registrert som arbeidssøker.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Ugyldig periode - fra og med dato må være før til dato"),
            @ApiResponse(responseCode = "403", description = "Ingen tilgang"),
            @ApiResponse(responseCode = "500", description = "Ukjent feil")})
    ArbeidssokerperioderDto hentArbeidssokerperioder(
            @Parameter(required = true, description = "Fødselsnummer") String fnr,
            @Parameter(required = true, description = "Fra og med dato") LocalDate fraOgMed,
            @Parameter(description = "Til og med dato") LocalDate tilOgMed
    );
}
