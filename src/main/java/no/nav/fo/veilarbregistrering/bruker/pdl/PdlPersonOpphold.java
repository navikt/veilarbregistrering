package no.nav.fo.veilarbregistrering.bruker.pdl;

public class PdlPersonOpphold {
    private Oppholdstype type;
    private String oppholdFra;
    private String oppholdTil;

    public PdlPersonOpphold() {
    }

    public Oppholdstype getType() {
        return type;
    }

    public void setType(Oppholdstype type) {
        this.type = type;
    }

    public String getOppholdFra() {
        return oppholdFra;
    }

    public void setOppholdFra(String oppholdFra) {
        this.oppholdFra = oppholdFra;
    }

    public String getOppholdTil() {
        return oppholdTil;
    }

    public void setOppholdTil(String oppholdTil) {
        this.oppholdTil = oppholdTil;
    }
}






