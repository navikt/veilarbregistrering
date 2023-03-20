package no.nav.fo.veilarbregistrering.oppgave.resources

import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService
import no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveMapper.map
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/oppgave")
class OppgaveResource(
    private val userService: UserService,
    private val oppgaveService: OppgaveService,
    private val tilgangskontrollService: TilgangskontrollService
) : OppgaveApi {

    @PostMapping
    override fun opprettOppgave(@RequestBody oppgaveDto: OppgaveDto?): OppgaveDto {
        if (oppgaveDto?.oppgaveType == null) { throw IllegalArgumentException("Oppgave m/ type må være angitt") }
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkSkrivetilgangTilBruker(bruker, "oppgave")
        val oppgaveResponse = oppgaveService.opprettOppgave(bruker, oppgaveDto.oppgaveType)
        return map(oppgaveResponse, oppgaveDto.oppgaveType)
    }
}