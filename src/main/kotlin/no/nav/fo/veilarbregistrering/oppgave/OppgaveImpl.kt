package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.bruker.AktorId
import java.time.LocalDateTime

class OppgaveImpl(
    val id: Long,
    val aktorId: AktorId,
    val oppgavetype: OppgaveType,
    val eksternOppgaveId: Long,
    opprettetTidspunkt: LocalDateTime
) {
    internal val opprettet: OppgaveOpprettet = OppgaveOpprettet(opprettetTidspunkt)
}