package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.time.LocalDateTime;

public class OppgaveImpl {

    private final long id;
    private final AktorId aktorId;
    private final OppgaveType oppgavetype;
    private final long eksternOppgaveId;
    private final LocalDateTime opprettet;

    public OppgaveImpl(
            long id,
            AktorId aktorId,
            OppgaveType oppgavetype,
            long eksternOppgaveId,
            LocalDateTime opprettet) {

        this.id = id;
        this.aktorId = aktorId;
        this.oppgavetype = oppgavetype;
        this.eksternOppgaveId = eksternOppgaveId;
        this.opprettet = opprettet;
    }

    public long getId() {
        return id;
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public OppgaveType getOppgavetype() {
        return oppgavetype;
    }

    public OppgaveOpprettet getOpprettet() {
        return new OppgaveOpprettet(opprettet);
    }

    public long getEksternOppgaveId() {
        return eksternOppgaveId;
    }
}
