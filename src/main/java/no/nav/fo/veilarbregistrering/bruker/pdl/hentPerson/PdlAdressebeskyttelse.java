package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

public class PdlAdressebeskyttelse {

    private PdlGradering gradering;

    public PdlAdressebeskyttelse() {
    }

    public PdlAdressebeskyttelse(PdlGradering gradering) {
        this.gradering = gradering;
    }

    public PdlGradering getGradering() {
        return gradering;
    }

    public void setGradering(PdlGradering gradering) {
        this.gradering = gradering;
    }

    public boolean erGradert() {
        return gradering != null && gradering != PdlGradering.UGRADERT;
    }
}
