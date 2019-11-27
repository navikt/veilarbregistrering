package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.util.Objects;

public class Servicegruppe implements Metric {

    private final String servicegruppe;

    static Servicegruppe of(String servicegruppe) {
        return servicegruppe != null ? new Servicegruppe(servicegruppe) : new NullableServicegruppe();
    }

    private Servicegruppe(String servicegruppe) {
        if (servicegruppe == null) {
            throw new IllegalArgumentException("Servicegruppe skal ikke kunne være null. " +
                    "Hvis null, skal NullableServicegruppe brukes i stedet.");
        }
        this.servicegruppe = servicegruppe;
    }

    @Override
    public String fieldName() {
        return "servicegruppe";
    }

    @Override
    public String value() {
        return servicegruppe;
    }

    public String stringValue() {
        return servicegruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servicegruppe that = (Servicegruppe) o;
        return Objects.equals(servicegruppe, that.servicegruppe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servicegruppe);
    }

    /**
     * <code>Null object</code> is an object with no referenced value or with defined neutral ("null") behavior
     */
    public static class NullableServicegruppe extends Servicegruppe {

        NullableServicegruppe() {
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
