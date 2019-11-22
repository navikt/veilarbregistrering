package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.util.Objects;

public class Formidlingsgruppe implements Metric {

    private final String formidlingsgruppe;

    static Formidlingsgruppe of(String formidlingsgruppe) {
        return formidlingsgruppe != null ? new Formidlingsgruppe(formidlingsgruppe) : new NullableFormidlingsgruppe();
    }

    private Formidlingsgruppe(String formidlingsgruppe) {
        if (formidlingsgruppe == null) {
            throw new IllegalArgumentException("Formidlingsgruppe skal ikke kunne være null. " +
                    "Hvis null, skal NullableFormdlingsgruppe brukes i stedet.");
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

    @Override
    public int hashCode() {
        return Objects.hash(formidlingsgruppe);
    }

    /**
     * <code>Null object</code> is an object with no referenced value or with defined neutral ("null") behavior
     */
    public static class NullableFormidlingsgruppe extends Formidlingsgruppe {

        NullableFormidlingsgruppe() {
            super("UKJENT");
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
