package no.nav.fo.veilarbregistrering.bruker;

import java.time.LocalDate;

public class Foedselsdato {

    private final LocalDate foedselsdato;

    public static Foedselsdato of(LocalDate foedselsdato) {
        return new Foedselsdato(foedselsdato);
    }

    private Foedselsdato(LocalDate foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    public LocalDate getFoedselsdato() {
        return foedselsdato;
    }

    public int getAlder() {
        return -1;
    }
}
