package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public class Kontaktinfo {

    private final String telefonnummerFraKrr;
    private Telefonnummer telefonnummerFraNav;

    public static Kontaktinfo of(String telefonnummerFraKrr, Telefonnummer telefonnummerFraNav) {
        return new Kontaktinfo(telefonnummerFraKrr, telefonnummerFraNav);
    }

    private Kontaktinfo(String telefonnummerFraKrr, Telefonnummer telefonnummerFraNav) {
        this.telefonnummerFraKrr = telefonnummerFraKrr;
        this.telefonnummerFraNav = telefonnummerFraNav;
    }

    public void oppdaterMedKontaktinfoFraNav(Telefonnummer telefonnummer) {
        telefonnummerFraNav = telefonnummer;
    }

    public Optional<String> getTelefonnummerFraKrr() {
        return Optional.ofNullable(telefonnummerFraKrr);
    }

    public Optional<Telefonnummer> getTelefonnummerFraNav() {
        return Optional.ofNullable(telefonnummerFraNav);
    }
}
