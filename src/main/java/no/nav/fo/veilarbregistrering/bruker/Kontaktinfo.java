package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public class Kontaktinfo {

    private final String telefonnummerFraKrr;
    private Telefonnummer telefonnummerFraNav;

    public static Kontaktinfo of(String telefon) {
        return new Kontaktinfo(telefon);
    }

    private Kontaktinfo(String telefonnummerFraKrr) {
        this.telefonnummerFraKrr = telefonnummerFraKrr;
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
