package no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter;

public class PdlIdent {
    private String ident;
    private boolean historisk;
    private Gruppe gruppe;

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

    public Gruppe getGruppe() {
        return gruppe;
    }

    public void setGruppe(Gruppe gruppe) {
        this.gruppe = gruppe;
    }
}
