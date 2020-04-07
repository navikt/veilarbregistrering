package no.nav.fo.veilarbregistrering.bruker;

public class Person {

    private final String opphold;
    private final String statsborgerskap;

    public static Person of(String opphold, String statsborgerskap) {
        return new Person(opphold, statsborgerskap);
    }

    private Person(String opphold, String statsborgerskap) {
        this.opphold = opphold;
        this.statsborgerskap = statsborgerskap;
    }

    @Override
    public String toString() {
        return "Person{" +
                "opphold='" + opphold + '\'' +
                ", statsborgerskap='" + statsborgerskap + '\'' +
                '}';
    }
}
