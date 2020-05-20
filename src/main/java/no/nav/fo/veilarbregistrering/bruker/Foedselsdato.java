package no.nav.fo.veilarbregistrering.bruker;

import java.time.LocalDate;
import java.time.Period;

public class Foedselsdato {

    private final LocalDate foedselsdato;

    public static Foedselsdato of(LocalDate foedselsdato) {
        return new Foedselsdato(foedselsdato);
    }

    protected Foedselsdato(LocalDate foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    public int alder() {
        return Period.between(foedselsdato, dagensDato()).getYears();
    }

    /**
     * For å kunne overstyre dagens dato i test
     */
    protected LocalDate dagensDato() {
        return LocalDate.now();
    }
}
