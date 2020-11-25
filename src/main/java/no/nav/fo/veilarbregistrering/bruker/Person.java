package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public class Person {

    private final Telefonnummer telefonnummer;
    private final Foedselsdato foedselsdato;
    private final AdressebeskyttelseGradering adressebeskyttelseGradering;

    public static Person of(
            Telefonnummer telefonnummer,
            Foedselsdato foedselsdato,
            AdressebeskyttelseGradering adressebeskyttelseGradering) {
        return new Person(telefonnummer, foedselsdato, adressebeskyttelseGradering);
    }

    private Person(Telefonnummer telefonnummer,
                   Foedselsdato foedselsdato,
                   AdressebeskyttelseGradering adressebeskyttelseGradering) {
        this.telefonnummer = telefonnummer;
        this.foedselsdato = foedselsdato;
        this.adressebeskyttelseGradering = adressebeskyttelseGradering;
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
}
