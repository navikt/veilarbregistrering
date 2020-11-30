package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;

public class OppgaveDto {

    long id;
    String tildeltEnhetsnr;
    OppgaveType oppgaveType;

    public OppgaveDto(long id, String tildeltEnhetsnr, OppgaveType oppgaveType) {
        this.id = id;
        this.tildeltEnhetsnr = tildeltEnhetsnr;
        this.oppgaveType = oppgaveType;
    }

    public OppgaveDto() {
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

    public void setId(long id) {
        this.id = id;
    }

    public void setTildeltEnhetsnr(String tildeltEnhetsnr) {
        this.tildeltEnhetsnr = tildeltEnhetsnr;
    }

    public void setOppgaveType(OppgaveType oppgaveType) {
        this.oppgaveType = oppgaveType;
    }

}
