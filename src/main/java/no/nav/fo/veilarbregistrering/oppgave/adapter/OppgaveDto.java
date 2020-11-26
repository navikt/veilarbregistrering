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

}
