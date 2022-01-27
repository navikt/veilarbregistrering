package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public class Kontaktinfo {

    private final String telefonnummerFraKrr;
    private Telefonnummer telefonnummerFraNav;
    private final Navn navn;

    public static Kontaktinfo of(String telefonnummerFraKrr, Telefonnummer telefonnummerFraNav, Navn navn) {
        return new Kontaktinfo(telefonnummerFraKrr, telefonnummerFraNav, navn);
    }

    private Kontaktinfo(String telefonnummerFraKrr, Telefonnummer telefonnummerFraNav, Navn navn) {
        this.telefonnummerFraKrr = telefonnummerFraKrr;
        this.telefonnummerFraNav = telefonnummerFraNav;
        this.navn = navn;
    }

    public void oppdaterMedKontaktinfoFraNav(Telefonnummer telefonnummer) {
        telefonnummerFraNav = telefonnummer;
    }

    public Navn getNavn() {
        return navn;
    }

    public Optional<String> getTelefonnummerFraKrr() {
        return Optional.ofNullable(telefonnummerFraKrr);
    }

    public Optional<Telefonnummer> getTelefonnummerFraNav() {
        return Optional.ofNullable(telefonnummerFraNav);
    }
}
