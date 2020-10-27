package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

public class PdlAdressebeskyttelse implements Comparable<PdlAdressebeskyttelse> {

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

    @Override
    public int compareTo(PdlAdressebeskyttelse other) {
        return other.gradering.getNiva() - this.gradering.getNiva();
    }
}
