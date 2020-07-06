package no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter;

public class HentIdenterVariables {

    private String ident;

    public HentIdenterVariables(String fnr) {
        this.ident = fnr;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }
}
