package no.nav.fo.veilarbregistrering.bruker;

import java.util.Date;

public class Foedselsdato {

    private Date foedselsdato;

    public static Foedselsdato of(Date foedselsdato) {
        return new Foedselsdato(foedselsdato);
    }

    private Foedselsdato(Date foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    public Date getFoedselsdato() {
        return foedselsdato;
    }

    public void setFoedselsdato(Date foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    public int getAlder() {
        return -1;
    }
}
