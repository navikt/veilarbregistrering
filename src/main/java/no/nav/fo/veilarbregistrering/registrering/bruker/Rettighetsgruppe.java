package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.util.Objects;

public class Rettighetsgruppe implements Metric {

    private final String rettighetsgruppe;

    static Rettighetsgruppe of(String rettighetsgruppe) {
        return rettighetsgruppe != null ? new Rettighetsgruppe(rettighetsgruppe) : new NullableRettighetsgruppe();
    }

    private Rettighetsgruppe(String rettighetsgruppe) {
        if (rettighetsgruppe == null) {
            throw new IllegalArgumentException("Rettighetsgruppe skal ikke kunne være null. " +
                    "Hvis null, skal NullableRettighetsgruppe brukes i stedet.");
        }
        this.rettighetsgruppe = rettighetsgruppe;
    }

    @Override
    public String fieldName() {
        return "rettighetsgruppe";
    }

    @Override
    public String value() {
        return rettighetsgruppe;
    }

    public String stringValue() {
        return rettighetsgruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rettighetsgruppe that = (Rettighetsgruppe) o;
        return Objects.equals(rettighetsgruppe, that.rettighetsgruppe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rettighetsgruppe);
    }

    /**
     * <code>Null object</code> is an object with no referenced value or with defined neutral ("null") behavior
     */
    public static class NullableRettighetsgruppe extends Rettighetsgruppe {

        NullableRettighetsgruppe() {
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
