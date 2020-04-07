package no.nav.fo.veilarbregistrering.bruker;

public class Person {

    private String opphold;
    private String statsborgerskap;

    public Person(String opphold, String statsborgerskap) {
        this.opphold = opphold;
        this.statsborgerskap = statsborgerskap;
    }

    public static Person of(String opphold, String statsborgerskap) {
        return new Person(opphold, statsborgerskap);
    }

    public String getStatsborgerskap() {
        return this.statsborgerskap;
    }

    public String getOpphold() {
        return this.opphold;
    }

    @Override
    public String toString() {
        return "Person{" +
                "opphold='" + opphold + '\'' +
                ", statsborgerskap='" + statsborgerskap + '\'' +
                '}';
    }
}
