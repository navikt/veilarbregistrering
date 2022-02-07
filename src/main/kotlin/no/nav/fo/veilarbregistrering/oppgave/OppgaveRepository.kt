package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.bruker.AktorId

interface OppgaveRepository {
    fun opprettOppgave(aktorId: AktorId, oppgaveType: OppgaveType, oppgaveId: Long): Long
    fun hentOppgaverFor(aktorId: AktorId): List<OppgaveImpl>
}