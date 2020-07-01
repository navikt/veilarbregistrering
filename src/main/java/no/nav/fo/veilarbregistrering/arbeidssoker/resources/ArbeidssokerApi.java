package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

import io.swagger.annotations.*;

import java.time.LocalDate;

@Api(value = "ArbeidssokerResource")
public interface ArbeidssokerApi {

    @ApiOperation(value = "Henter alle perioder hvor bruker er registrert som arbeidssøker.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Ugyldig periode - fra og med dato må være før til dato"),
            @ApiResponse(code = 403, message = "Ingen tilgang"),
            @ApiResponse(code = 500, message = "Ukjent feil")})
    ArbeidssokerperioderDto hentArbeidssokerperioder(
            @ApiParam(required = true, value = "Fødselsnummer") String fnr,
            @ApiParam(required = true, value = "Fra og med dato") LocalDate fraOgMed,
            @ApiParam(value = "Til og med dato") LocalDate tilOgMed
    );
}
