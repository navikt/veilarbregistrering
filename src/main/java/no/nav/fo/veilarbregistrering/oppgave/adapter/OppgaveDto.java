package no.nav.fo.veilarbregistrering.oppgave.adapter;

public class OppgaveDto {

    private String aktoerId;
    private String beskrivelse;
    private String tema;
    private String oppgavetype;
    private String fristFerdigstillelse;
    private String aktivDato;
    private String prioritet;

    public OppgaveDto() {}

    public String getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(String aktoerId) {
        this.aktoerId = aktoerId;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getOppgavetype() {
        return oppgavetype;
    }

    public void setOppgavetype(String oppgavetype) {
        this.oppgavetype = oppgavetype;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getFristFerdigstillelse() {
        return fristFerdigstillelse;
    }

    public void setFristFerdigstillelse(String fristFerdigstillelse) {
        this.fristFerdigstillelse = fristFerdigstillelse;
    }

    public String getAktivDato() {
        return aktivDato;
    }

    public void setAktivDato(String aktivDato) {
        this.aktivDato = aktivDato;
    }

    public String getPrioritet() {
        return prioritet;
    }

    public void setPrioritet(String prioritet) {
        this.prioritet = prioritet;
    }
}
