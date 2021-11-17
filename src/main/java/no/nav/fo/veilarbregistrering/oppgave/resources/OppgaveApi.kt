package no.nav.fo.veilarbregistrering.oppgave.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "OppgaveResource")
public interface OppgaveApi {

    @Operation(summary = "Oppretter 'kontakt bruker'-oppgave på vegne av bruker.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oppgave opprettet OK"),
            @ApiResponse(responseCode = "403", description = "Duplikat - fant tilsvarende oppgave " +
                    "som ble opprettet innenfor de siste 2 arbeidsdager"),
            @ApiResponse(responseCode = "500", description = "Ukjent teknisk feil")
    })
    OppgaveDto opprettOppgave(OppgaveDto oppgaveDto);
}
