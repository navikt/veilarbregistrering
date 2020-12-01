package no.nav.fo.veilarbregistrering.oppgave.adapter;

class OppgaveDto {

    private final String aktoerId;
    private final String beskrivelse;
    private final String tema;
    private final String oppgavetype;
    private final String fristFerdigstillelse;
    private final String aktivDato;
    private final String prioritet;
    private final String tildeltEnhetsnr;

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

}
