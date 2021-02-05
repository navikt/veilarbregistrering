package no.nav.fo.veilarbregistrering.oppgave.resources

import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType

internal object OppgaveMapper {
    @JvmStatic
    fun map(oppgaveResponse: OppgaveResponse, oppgaveType: OppgaveType): OppgaveDto =
            OppgaveDto(oppgaveResponse.id, oppgaveResponse.tildeltEnhetsnr, oppgaveType)
}