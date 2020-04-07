package no.nav.fo.veilarbregistrering.bruker.pdl;

public class Variables {

    private String ident;

    private boolean oppholdHistorikk;

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public boolean isOppholdHistorikk() {
        return oppholdHistorikk;
    }

    public void setOppholdHistorikk(boolean oppholdHistorikk) {
        this.oppholdHistorikk = oppholdHistorikk;
    }

    public Variables(String ident, boolean oppholdHistorikk) {
        this.ident = ident;
        this.oppholdHistorikk = oppholdHistorikk;
    }


}
