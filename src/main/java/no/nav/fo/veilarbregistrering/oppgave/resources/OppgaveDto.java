package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;

public class OppgaveDto {

    private final long id;
    private final String tildeltEnhetsnr;
    private final OppgaveType oppgaveType;

    public OppgaveDto(long id, String tildeltEnhetsnr, OppgaveType oppgaveType) {
        this.id = id;
        this.tildeltEnhetsnr = tildeltEnhetsnr;
        this.oppgaveType = oppgaveType;
    }

    public long getId() {
        return this.id;
    }

    public String getTildeltEnhetsnr() {
        return this.tildeltEnhetsnr;
    }

    public OppgaveType getOppgaveType() {
        return this.oppgaveType;
    }

}
