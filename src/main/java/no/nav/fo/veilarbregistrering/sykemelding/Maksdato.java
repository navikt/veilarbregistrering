package no.nav.fo.veilarbregistrering.sykemelding;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Maksdato {

    private final String maksdato;

    private Maksdato(String maksdato) {
        this.maksdato = maksdato;
    }

    public static Maksdato of(String maksdato) {
        return new Maksdato(maksdato);
    }

    boolean beregnSykmeldtMellom39Og52Uker(LocalDate dagenDato) {
        if (maksdato == null) {
            return false;
        }
        LocalDate dato = LocalDate.parse(maksdato);
        long GJENSTAENDE_UKER = 13;

        return ChronoUnit.WEEKS.between(dagenDato, dato) >= 0 &&
                ChronoUnit.WEEKS.between(dagenDato, dato) <= GJENSTAENDE_UKER;
    }

    String asString() {
        return maksdato;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Maksdato maksdato1 = (Maksdato) o;
        return Objects.equals(maksdato, maksdato1.maksdato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maksdato);
    }
}
