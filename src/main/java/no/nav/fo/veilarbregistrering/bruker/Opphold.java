package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metric;

public class Opphold implements Metric {

    private final Oppholdstype type;
    private final Periode periode;

    public static Opphold of(Oppholdstype type, Periode periode) {
        return new Opphold(type, periode);
    }

    public static Opphold.NullableOpphold nullable() {
        return new Opphold.NullableOpphold();
    }

    private Opphold(Oppholdstype type, Periode periode) {
        this.type = type;
        this.periode = periode;
    }

    public Oppholdstype getType() {
        return type;
    }

    @Override
    public String fieldName() {
        return "oppholdstype";
    }

    @Override
    public Object value() {
        return type.toString();
    }

    @Override
    public String toString() {
        return "Opphold{" +
                "type=" + type +
                ", periode=" + periode +
                '}';
    }

    /**
     * <code>Null object</code> is an object with no referenced value or with defined neutral ("null") behavior
     */
    public static class NullableOpphold extends Opphold {

        NullableOpphold() {
            super(Oppholdstype.UKJENT, null);
        }

        @Override
        public String value() {
            return "INGEN_VERDI";
        }
    }

    public enum Oppholdstype {
        MIDLERTIDIG,
        PERMANENT,
        OPPLYSNING_MANGLER,
        UKJENT
    }
}
