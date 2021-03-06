package no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter;

public class PdlIdent {
    private String ident;
    private boolean historisk;
    private PdlGruppe gruppe;

    public PdlIdent() {
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public boolean isHistorisk() {
        return historisk;
    }

    public void setHistorisk(boolean historisk) {
        this.historisk = historisk;
    }

    public PdlGruppe getGruppe() {
        return gruppe;
    }

    public void setGruppe(PdlGruppe gruppe) {
        this.gruppe = gruppe;
    }
}
