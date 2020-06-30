package no.nav.fo.veilarbregistrering.oppgave.resources;

import io.swagger.annotations.*;

@Api(value = "OppgaveResource")
public interface OppgaveApi {

    @ApiOperation(value = "Oppretter 'kontakt bruker'-oppgave på vegne av bruker.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Oppgave opprettet OK"),
            @ApiResponse(code = 403, message = "Duplikat - fant tilsvarende oppgave " +
                    "som ble opprettet innenfor de siste 2 arbeidsdager"),
            @ApiResponse(code = 500, message = "Ukjent teknisk feil")
    })
    OppgaveDto opprettOppgave(OppgaveDto oppgaveDto);
}
