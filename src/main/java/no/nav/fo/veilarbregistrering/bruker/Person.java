package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public class Person {

    private final Opphold opphold;
    private final Statsborgerskap statsborgerskap;
    private final Telefonnummer telefonnummer;
    private final Foedselsdato foedselsdato;
    private final AdressebeskyttelseGradering adressebeskyttelseGradering;

    public static Person of(
            Opphold opphold,
            Statsborgerskap statsborgerskap,
            Telefonnummer telefonnummer,
            Foedselsdato foedselsdato,
            AdressebeskyttelseGradering adressebeskyttelseGradering) {
        return new Person(opphold, statsborgerskap, telefonnummer, foedselsdato, adressebeskyttelseGradering);
    }

    private Person(Opphold opphold,
                   Statsborgerskap statsborgerskap,
                   Telefonnummer telefonnummer,
                   Foedselsdato foedselsdato,
                   AdressebeskyttelseGradering adressebeskyttelseGradering) {
        this.opphold = opphold;
        this.statsborgerskap = statsborgerskap;
        this.telefonnummer = telefonnummer;
        this.foedselsdato = foedselsdato;
        this.adressebeskyttelseGradering = adressebeskyttelseGradering;
    }

    public Opphold getOpphold() {
        return opphold != null ? opphold : Opphold.nullable();
    }

    public Statsborgerskap getStatsborgerskap() {
        return statsborgerskap;
    }

    public Optional<Telefonnummer> getTelefonnummer() {
        return Optional.ofNullable(telefonnummer);
    }

    public Foedselsdato getFoedselsdato() {
        return foedselsdato;
    }

    public AdressebeskyttelseGradering getAdressebeskyttelseGradering() {
        return adressebeskyttelseGradering;
    }

    public boolean harAdressebeskyttelse() {
        return adressebeskyttelseGradering != null && adressebeskyttelseGradering.erGradert();
    }

    @Override
    public String toString() {
        return "Person{" +
                "opphold='" + opphold + '\'' +
                ", statsborgerskap='" + statsborgerskap + '\'' +
                '}';
    }

}
