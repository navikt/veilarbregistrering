package no.nav.fo.veilarbregistrering.bruker.pdl;

public class Variables {

    private String ident;

    private boolean navnHistorikk;

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public boolean isNavnHistorikk() {
        return navnHistorikk;
    }

    public void setNavnHistorikk(boolean navnHistorikk) {
        this.navnHistorikk = navnHistorikk;
    }

    public Variables(String ident, boolean navnHistorikk) {
        this.ident = ident;
        this.navnHistorikk = navnHistorikk;
    }


}
