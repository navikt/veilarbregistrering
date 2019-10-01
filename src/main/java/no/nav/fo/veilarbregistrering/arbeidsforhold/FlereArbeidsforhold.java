package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;

public class FlereArbeidsforhold {

    private final List<Arbeidsforhold> flereArbeidsforhold;

    private FlereArbeidsforhold(List<Arbeidsforhold> flereArbeidsforhold) {
        this.flereArbeidsforhold = flereArbeidsforhold;
    }

    public static FlereArbeidsforhold of(List<Arbeidsforhold> flereArbeidsforhold) {
        return new FlereArbeidsforhold(flereArbeidsforhold != null ? flereArbeidsforhold : Collections.emptyList());
    }

    public Arbeidsforhold siste() {
        return flereArbeidsforhold.stream()
                .sorted(sorterArbeidsforholdEtterTilDato()
                        .thenComparing(comparing(Arbeidsforhold::getFom)))
                .findFirst()
                .orElse(Arbeidsforhold.utenStyrkkode());
    }

    private static Comparator<Arbeidsforhold> sorterArbeidsforholdEtterTilDato() {
        return comparing(Arbeidsforhold::getTom, nullsLast(Comparator.naturalOrder()))
                .reversed();
    }

    boolean harArbeidsforholdPaaDato(LocalDate innevaerendeMnd) {
        return flereArbeidsforhold.stream()
                .map(arbeidsforhold -> arbeidsforhold.erDatoInnenforPeriode(innevaerendeMnd))
                .filter(b -> b)
                .findAny().orElse(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlereArbeidsforhold that = (FlereArbeidsforhold) o;
        return Objects.equals(flereArbeidsforhold, that.flereArbeidsforhold);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flereArbeidsforhold);
    }

}
