package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class Arbeidssokerperiode {

    private final Formidlingsgruppe formidlingsgruppe;
    private final Periode periode;

    public static Arbeidssokerperiode of(Formidlingsgruppe formidlingsgruppe, Periode periode) {
        return new Arbeidssokerperiode(formidlingsgruppe, periode);
    }

    public Arbeidssokerperiode(Formidlingsgruppe formidlingsgruppe, Periode periode) {
        this.formidlingsgruppe = formidlingsgruppe;
        this.periode = periode;
    }

    public Arbeidssokerperiode tilOgMed(LocalDate nyTildato) {
        return new Arbeidssokerperiode(formidlingsgruppe, periode.tilOgMed(nyTildato));
    }

    public Periode getPeriode() {
        return periode;
    }

    public Formidlingsgruppe getFormidlingsgruppe() {
        return formidlingsgruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidssokerperiode that = (Arbeidssokerperiode) o;
        return Objects.equals(formidlingsgruppe, that.formidlingsgruppe) &&
                Objects.equals(periode, that.periode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formidlingsgruppe, periode);
    }

    @Override
    public String toString() {
        return "{" +
                "formidlingsgruppe=" + formidlingsgruppe +
                ", periode=" + periode +
                '}';
    }

    static class EldsteFoerst implements Comparator<Arbeidssokerperiode> {

        static EldsteFoerst eldsteFoerst() {
            return new EldsteFoerst();
        }

        @Override
        public int compare(Arbeidssokerperiode t0, Arbeidssokerperiode t1) {
            return t0.getPeriode().compareTo(t1.getPeriode());
        }

    }
}
