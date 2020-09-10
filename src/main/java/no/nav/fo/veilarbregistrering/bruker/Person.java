package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public class Person {

    private final Opphold opphold;
    private final Statsborgerskap statsborgerskap;
    private final Telefonnummer telefonnummer;
    private final Foedselsdato foedselsdato;
    private final GeografiskTilknytning geografiskTilknytning;

    public static Person of(
            Opphold opphold,
            Statsborgerskap statsborgerskap,
            Telefonnummer telefonnummer,
            Foedselsdato foedselsdato,
            GeografiskTilknytning geografiskTilknytning) {
        return new Person(opphold, statsborgerskap, telefonnummer, foedselsdato, geografiskTilknytning);
    }

    private Person(Opphold opphold,
                   Statsborgerskap statsborgerskap,
                   Telefonnummer telefonnummer,
                   Foedselsdato foedselsdato,
                   GeografiskTilknytning geografiskTilknytning) {
        this.opphold = opphold;
        this.statsborgerskap = statsborgerskap;
        this.telefonnummer = telefonnummer;
        this.foedselsdato = foedselsdato;
        this.geografiskTilknytning = geografiskTilknytning;
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
                ", geografiskTilknytning='" + geografiskTilknytning + '\'' +
                '}';
    }

    public Optional<Telefonnummer> getTelefonnummer() {
        return Optional.ofNullable(telefonnummer);
    }

    public Foedselsdato getFoedselsdato() {
        return foedselsdato;
    }
}
