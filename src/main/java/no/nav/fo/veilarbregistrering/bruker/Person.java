package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public class Person {

    private final Telefonnummer telefonnummer;
    private final Foedselsdato foedselsdato;
    private final AdressebeskyttelseGradering adressebeskyttelseGradering;
    private final Navn navn;

    public static Person of(
            Telefonnummer telefonnummer,
            Foedselsdato foedselsdato,
            AdressebeskyttelseGradering adressebeskyttelseGradering,
            Navn navn) {
        return new Person(telefonnummer, foedselsdato, adressebeskyttelseGradering, navn);
    }

    private Person(Telefonnummer telefonnummer,
                   Foedselsdato foedselsdato,
                   AdressebeskyttelseGradering adressebeskyttelseGradering,
                   Navn navn) {
        this.telefonnummer = telefonnummer;
        this.foedselsdato = foedselsdato;
        this.adressebeskyttelseGradering = adressebeskyttelseGradering;
        this.navn = navn;
    }

    public Optional<Telefonnummer> getTelefonnummer() {
        return Optional.ofNullable(telefonnummer);
    }

    public Foedselsdato getFoedselsdato() {
        return foedselsdato;
    }

    public Navn getNavn() {
        return navn;
    }

    public AdressebeskyttelseGradering getAdressebeskyttelseGradering() {
        return adressebeskyttelseGradering;
    }

    public boolean harAdressebeskyttelse() {
        return adressebeskyttelseGradering != null && adressebeskyttelseGradering.erGradert();
    }
}
