package no.nav.fo.veilarbregistrering.oppgave.resources

import no.nav.fo.veilarbregistrering.oppgave.OppgaveType

data class OppgaveDto(val id: Long, val tildeltEnhetsnr: String?, val oppgaveType: OppgaveType)