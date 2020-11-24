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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OppgaveDto)) return false;
        final OppgaveDto other = (OppgaveDto) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$tildeltEnhetsnr = this.getTildeltEnhetsnr();
        final Object other$tildeltEnhetsnr = other.getTildeltEnhetsnr();
        if (this$tildeltEnhetsnr == null ? other$tildeltEnhetsnr != null : !this$tildeltEnhetsnr.equals(other$tildeltEnhetsnr))
            return false;
        final Object this$oppgaveType = this.getOppgaveType();
        final Object other$oppgaveType = other.getOppgaveType();
        if (this$oppgaveType == null ? other$oppgaveType != null : !this$oppgaveType.equals(other$oppgaveType))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OppgaveDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.getId();
        result = result * PRIME + (int) ($id >>> 32 ^ $id);
        final Object $tildeltEnhetsnr = this.getTildeltEnhetsnr();
        result = result * PRIME + ($tildeltEnhetsnr == null ? 43 : $tildeltEnhetsnr.hashCode());
        final Object $oppgaveType = this.getOppgaveType();
        result = result * PRIME + ($oppgaveType == null ? 43 : $oppgaveType.hashCode());
        return result;
    }

    public String toString() {
        return "OppgaveDto(id=" + this.getId() + ", tildeltEnhetsnr=" + this.getTildeltEnhetsnr() + ", oppgaveType=" + this.getOppgaveType() + ")";
    }
}
