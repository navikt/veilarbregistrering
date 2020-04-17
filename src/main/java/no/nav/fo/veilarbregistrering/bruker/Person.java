package no.nav.fo.veilarbregistrering.bruker;

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
        return opphold != null ? opphold : Opphold.nullable();
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

}
