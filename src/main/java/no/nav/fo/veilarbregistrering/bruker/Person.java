package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.time.LocalDate;

public class Person {

    private final Opphold opphold;
    private final Statsborgerskap statsborgerskap;

    public static Person of(Opphold opphold, Statsborgerskap statsborgerskap) {
        return new Person(opphold, statsborgerskap);
    }

    private Person(Opphold opphold, Statsborgerskap statsborgerskap) {
        this.opphold = opphold;
        this.statsborgerskap = statsborgerskap;
    }

    public Opphold getOpphold() {
        return opphold;
    }

    public Statsborgerskap getStatsborgerskap() {
        return statsborgerskap;
    }

    @Override
    public String toString() {
        return "Person{" +
                "opphold='" + opphold + '\'' +
                ", statsborgerskap='" + statsborgerskap + '\'' +
                '}';
    }

    public static class Opphold implements Metric {

        private final Oppholdstype type;
        private final Periode periode;

        public static Opphold of(Oppholdstype type, Periode periode) {
            return new Opphold(type, periode);
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
    }

    public enum Oppholdstype {
        MIDLERTIDIG,
        PERMANENT,
        OPPLYSNING_MANGLER
    }

    public static class Statsborgerskap implements Metric {

        private final String statsborgerskap;
        private final Periode periode;

        public static Statsborgerskap of(String statsborgerskap, Periode periode) {
            return new Statsborgerskap(statsborgerskap, periode);
        }
        private Statsborgerskap(String statsborgerskap, Periode periode) {
            this.statsborgerskap = statsborgerskap;
            this.periode = periode;
        }

        public String getStatsborgerskap() {
            return statsborgerskap;
        }

        @Override
        public String toString() {
            return "Statsborgerskap{" +
                    "statsborgerskap='" + statsborgerskap + '\'' +
                    ", periode=" + periode +
                    '}';
        }

        @Override
        public String fieldName() {
            return "statsborgerskap";
        }

        @Override
        public Object value() {
            return statsborgerskap;
        }
    }
    public static class Periode {

        private final LocalDate fra;
        private final LocalDate til;

        public static Periode of(LocalDate fra, LocalDate til) {
            return new Periode(fra, til);
        }
        private Periode(LocalDate fra, LocalDate til) {
            this.fra = fra;
            this.til = til;
        }

        @Override
        public String toString() {
            return "Periode{" +
                    "fra=" + fra +
                    ", til=" + til +
                    '}';
        }
    }
}
