package no.nav.fo.veilarbregistrering.oppgave.adapter;

class OppgaveDto {

    private String aktoerId;
    private String beskrivelse;
    private String tema;
    private String oppgavetype;
    private String fristFerdigstillelse;
    private String aktivDato;
    private String prioritet;
    private String tildeltEnhetsnr;

    public OppgaveDto(String aktoerId, String beskrivelse, String tema, String oppgavetype, String fristFerdigstillelse, String aktivDato, String prioritet, String tildeltEnhetsnr) {
        this.aktoerId = aktoerId;
        this.beskrivelse = beskrivelse;
        this.tema = tema;
        this.oppgavetype = oppgavetype;
        this.fristFerdigstillelse = fristFerdigstillelse;
        this.aktivDato = aktivDato;
        this.prioritet = prioritet;
        this.tildeltEnhetsnr = tildeltEnhetsnr;
    }

    public OppgaveDto() {
    }

    public String getAktoerId() {
        return this.aktoerId;
    }

    public String getBeskrivelse() {
        return this.beskrivelse;
    }

    public String getTema() {
        return this.tema;
    }

    public String getOppgavetype() {
        return this.oppgavetype;
    }

    public String getFristFerdigstillelse() {
        return this.fristFerdigstillelse;
    }

    public String getAktivDato() {
        return this.aktivDato;
    }

    public String getPrioritet() {
        return this.prioritet;
    }

    public String getTildeltEnhetsnr() {
        return this.tildeltEnhetsnr;
    }

    public void setAktoerId(String aktoerId) {
        this.aktoerId = aktoerId;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public void setOppgavetype(String oppgavetype) {
        this.oppgavetype = oppgavetype;
    }

    public void setFristFerdigstillelse(String fristFerdigstillelse) {
        this.fristFerdigstillelse = fristFerdigstillelse;
    }

    public void setAktivDato(String aktivDato) {
        this.aktivDato = aktivDato;
    }

    public void setPrioritet(String prioritet) {
        this.prioritet = prioritet;
    }

    public void setTildeltEnhetsnr(String tildeltEnhetsnr) {
        this.tildeltEnhetsnr = tildeltEnhetsnr;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OppgaveDto)) return false;
        final OppgaveDto other = (OppgaveDto) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$aktoerId = this.getAktoerId();
        final Object other$aktoerId = other.getAktoerId();
        if (this$aktoerId == null ? other$aktoerId != null : !this$aktoerId.equals(other$aktoerId)) return false;
        final Object this$beskrivelse = this.getBeskrivelse();
        final Object other$beskrivelse = other.getBeskrivelse();
        if (this$beskrivelse == null ? other$beskrivelse != null : !this$beskrivelse.equals(other$beskrivelse))
            return false;
        final Object this$tema = this.getTema();
        final Object other$tema = other.getTema();
        if (this$tema == null ? other$tema != null : !this$tema.equals(other$tema)) return false;
        final Object this$oppgavetype = this.getOppgavetype();
        final Object other$oppgavetype = other.getOppgavetype();
        if (this$oppgavetype == null ? other$oppgavetype != null : !this$oppgavetype.equals(other$oppgavetype))
            return false;
        final Object this$fristFerdigstillelse = this.getFristFerdigstillelse();
        final Object other$fristFerdigstillelse = other.getFristFerdigstillelse();
        if (this$fristFerdigstillelse == null ? other$fristFerdigstillelse != null : !this$fristFerdigstillelse.equals(other$fristFerdigstillelse))
            return false;
        final Object this$aktivDato = this.getAktivDato();
        final Object other$aktivDato = other.getAktivDato();
        if (this$aktivDato == null ? other$aktivDato != null : !this$aktivDato.equals(other$aktivDato)) return false;
        final Object this$prioritet = this.getPrioritet();
        final Object other$prioritet = other.getPrioritet();
        if (this$prioritet == null ? other$prioritet != null : !this$prioritet.equals(other$prioritet)) return false;
        final Object this$tildeltEnhetsnr = this.getTildeltEnhetsnr();
        final Object other$tildeltEnhetsnr = other.getTildeltEnhetsnr();
        if (this$tildeltEnhetsnr == null ? other$tildeltEnhetsnr != null : !this$tildeltEnhetsnr.equals(other$tildeltEnhetsnr))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OppgaveDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $aktoerId = this.getAktoerId();
        result = result * PRIME + ($aktoerId == null ? 43 : $aktoerId.hashCode());
        final Object $beskrivelse = this.getBeskrivelse();
        result = result * PRIME + ($beskrivelse == null ? 43 : $beskrivelse.hashCode());
        final Object $tema = this.getTema();
        result = result * PRIME + ($tema == null ? 43 : $tema.hashCode());
        final Object $oppgavetype = this.getOppgavetype();
        result = result * PRIME + ($oppgavetype == null ? 43 : $oppgavetype.hashCode());
        final Object $fristFerdigstillelse = this.getFristFerdigstillelse();
        result = result * PRIME + ($fristFerdigstillelse == null ? 43 : $fristFerdigstillelse.hashCode());
        final Object $aktivDato = this.getAktivDato();
        result = result * PRIME + ($aktivDato == null ? 43 : $aktivDato.hashCode());
        final Object $prioritet = this.getPrioritet();
        result = result * PRIME + ($prioritet == null ? 43 : $prioritet.hashCode());
        final Object $tildeltEnhetsnr = this.getTildeltEnhetsnr();
        result = result * PRIME + ($tildeltEnhetsnr == null ? 43 : $tildeltEnhetsnr.hashCode());
        return result;
    }

    public String toString() {
        return "OppgaveDto(aktoerId=" + this.getAktoerId() + ", beskrivelse=" + this.getBeskrivelse() + ", tema=" + this.getTema() + ", oppgavetype=" + this.getOppgavetype() + ", fristFerdigstillelse=" + this.getFristFerdigstillelse() + ", aktivDato=" + this.getAktivDato() + ", prioritet=" + this.getPrioritet() + ", tildeltEnhetsnr=" + this.getTildeltEnhetsnr() + ")";
    }
}
