package no.nav.fo.veilarbregistrering.sykemelding;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
}
