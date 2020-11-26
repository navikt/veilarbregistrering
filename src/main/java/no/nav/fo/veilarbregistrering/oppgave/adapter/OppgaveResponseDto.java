package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;

class OppgaveResponseDto implements OppgaveResponse {

    private long id;
    private String tildeltEnhetsnr;

    public OppgaveResponseDto(long id, String tildeltEnhetsnr) {
        this.id = id;
        this.tildeltEnhetsnr = tildeltEnhetsnr;
    }

    public OppgaveResponseDto() {
    }

    public long getId() {
        return this.id;
    }

    public String getTildeltEnhetsnr() {
        return this.tildeltEnhetsnr;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTildeltEnhetsnr(String tildeltEnhetsnr) {
        this.tildeltEnhetsnr = tildeltEnhetsnr;
    }

}
