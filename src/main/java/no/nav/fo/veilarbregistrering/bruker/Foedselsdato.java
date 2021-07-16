package no.nav.fo.veilarbregistrering.bruker;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Foedselsdato that = (Foedselsdato) o;
        return Objects.equals(foedselsdato, that.foedselsdato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foedselsdato);
    }
}
