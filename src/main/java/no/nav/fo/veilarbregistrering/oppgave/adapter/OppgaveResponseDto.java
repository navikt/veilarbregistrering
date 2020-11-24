package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;

class OppgaveResponseDto implements OppgaveResponse {

    long id;
    String tildeltEnhetsnr;

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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OppgaveResponseDto)) return false;
        final OppgaveResponseDto other = (OppgaveResponseDto) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$tildeltEnhetsnr = this.getTildeltEnhetsnr();
        final Object other$tildeltEnhetsnr = other.getTildeltEnhetsnr();
        if (this$tildeltEnhetsnr == null ? other$tildeltEnhetsnr != null : !this$tildeltEnhetsnr.equals(other$tildeltEnhetsnr))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OppgaveResponseDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.getId();
        result = result * PRIME + (int) ($id >>> 32 ^ $id);
        final Object $tildeltEnhetsnr = this.getTildeltEnhetsnr();
        result = result * PRIME + ($tildeltEnhetsnr == null ? 43 : $tildeltEnhetsnr.hashCode());
        return result;
    }

    public String toString() {
        return "OppgaveResponseDto(id=" + this.getId() + ", tildeltEnhetsnr=" + this.getTildeltEnhetsnr() + ")";
    }
}
