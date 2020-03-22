package no.nav.fo.veilarbregistrering.bruker.pdl;

import java.time.LocalDate;

public class PdlPersonOpphold {
    private Oppholdstype type;
    private LocalDate oppholdFra;
    private LocalDate oppholdTil;

    public PdlPersonOpphold() {
    }

    public Oppholdstype getType() {
        return type;
    }

    public void setType(Oppholdstype type) {
        this.type = type;
    }

    public LocalDate getOppholdFra() {
        return oppholdFra;
    }

    public void setOppholdFra(LocalDate oppholdFra) {
        this.oppholdFra = oppholdFra;
    }

    public LocalDate getOppholdTil() {
        return oppholdTil;
    }

    public void setOppholdTil(LocalDate oppholdTil) {
        this.oppholdTil = oppholdTil;
    }
}






