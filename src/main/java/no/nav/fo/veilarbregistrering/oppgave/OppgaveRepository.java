package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

public interface OppgaveRepository {

    long opprettOppgave(AktorId aktørId, OppgaveType oppgaveType, long oppgaveId);

    OppgaveImpl hentOppgaveFor(AktorId valueOf);
}
