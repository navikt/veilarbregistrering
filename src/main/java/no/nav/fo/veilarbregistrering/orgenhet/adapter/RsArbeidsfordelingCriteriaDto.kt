package no.nav.fo.veilarbregistrering.orgenhet.adapter;

public class RsArbeidsfordelingCriteriaDto {

    static final String KONTAKT_BRUKER = "KONT_BRUK";
    static final String OPPFOLGING = "OPP";

    private String geografiskOmraade;
    private String oppgavetype;
    private String tema;

    public RsArbeidsfordelingCriteriaDto() {}

    public String getGeografiskOmraade() {
        return geografiskOmraade;
    }

    public void setGeografiskOmraade(String geografiskOmraade) {
        this.geografiskOmraade = geografiskOmraade;
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
}
