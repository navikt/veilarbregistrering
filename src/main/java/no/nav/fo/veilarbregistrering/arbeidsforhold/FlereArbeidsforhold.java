package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;

public class FlereArbeidsforhold {

    static int antallMnd = 12;
    static int minAntallMndSammenhengendeJobb = 6;
    static int dagIMnd = 1;

    private final List<Arbeidsforhold> flereArbeidsforhold;

    private FlereArbeidsforhold(List<Arbeidsforhold> flereArbeidsforhold) {
        this.flereArbeidsforhold = flereArbeidsforhold;
    }

    public static FlereArbeidsforhold of(List<Arbeidsforhold> flereArbeidsforhold) {
        return new FlereArbeidsforhold(flereArbeidsforhold != null ? flereArbeidsforhold : Collections.emptyList());
    }

    /**
     * En bruker som har jobbet sammenhengende i seks av de siste tolv månedene oppfyller betingelsen om arbeidserfaring
     */
    public boolean harJobbetSammenhengendeSeksAvTolvSisteManeder(LocalDate dagensDato) {

        int antallSammenhengendeMandeder = 0;
        int mndFraDagensMnd = 0;
        LocalDate innevaerendeMnd = LocalDate.of(dagensDato.getYear(), dagensDato.getMonthValue(), dagIMnd);

        while (antallSammenhengendeMandeder < minAntallMndSammenhengendeJobb && mndFraDagensMnd < antallMnd) {

            if (harArbeidsforholdPaaDato(innevaerendeMnd)) {
                antallSammenhengendeMandeder += 1;
            } else {
                antallSammenhengendeMandeder = 0;
            }

            innevaerendeMnd = innevaerendeMnd.minusMonths(1);
            mndFraDagensMnd += 1;
        }
        return antallSammenhengendeMandeder >= minAntallMndSammenhengendeJobb;
    }

    public Arbeidsforhold siste() {
        return flereArbeidsforhold.stream()
                .min(sorterArbeidsforholdEtterTilDato()
                .thenComparing(Arbeidsforhold::getFom))
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
