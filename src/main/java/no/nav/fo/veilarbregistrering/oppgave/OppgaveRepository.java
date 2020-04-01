package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.util.List;

public interface OppgaveRepository {

    long opprettOppgave(AktorId aktørId, OppgaveType oppgaveType, long oppgaveId);

    List<OppgaveImpl> hentOppgaverFor(AktorId aktørId);
}
