package no.nav.fo.veilarbregistrering.oppfolging;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.util.Objects;

public class Formidlingsgruppe implements Metric {

    private final String formidlingsgruppe;

    public static Formidlingsgruppe of(String formidlingsgruppe) {
        return new Formidlingsgruppe(formidlingsgruppe);
    }

    private Formidlingsgruppe(String formidlingsgruppe) {
        if (formidlingsgruppe == null) {
            throw new IllegalArgumentException("Formidlingsgruppe skal ikke kunne være null. " +
                    "Hvis null, kan NullableFormdlingsgruppe brukes i stedet.");
        }
        this.formidlingsgruppe = formidlingsgruppe;
    }

    @Override
    public String fieldName() {
        return "formidlingsgruppe";
    }

    @Override
    public String value() {
        return formidlingsgruppe;
    }

    public String stringValue() {
        return formidlingsgruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Formidlingsgruppe that = (Formidlingsgruppe) o;
        return Objects.equals(formidlingsgruppe, that.formidlingsgruppe);
    }

    public static NullableFormidlingsgruppe nullable() {
        return new NullableFormidlingsgruppe();
    }

    @Override
    public int hashCode() {
        return Objects.hash(formidlingsgruppe);
    }

    /**
     * <code>Null object</code> is an object with no referenced value or with defined neutral ("null") behavior
     */
    public static class NullableFormidlingsgruppe extends Formidlingsgruppe {

        private NullableFormidlingsgruppe() {
            super("INGEN_VERDI");
        }

        @Override
        public String stringValue() {
            return null;
        }

        @Override
        public String value() {
            return "INGEN_VERDI";
        }
    }
}
