package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metric;

public class Statsborgerskap implements Metric {

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
