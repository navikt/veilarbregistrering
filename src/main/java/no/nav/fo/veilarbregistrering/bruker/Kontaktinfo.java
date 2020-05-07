package no.nav.fo.veilarbregistrering.bruker;

public class Kontaktinfo {

    private final String telefon;

    public static Kontaktinfo of(String telefon) {
        return new Kontaktinfo(telefon);
    }

    private Kontaktinfo(String telefon) {
        this.telefon = telefon;
    }

    public String getTelefon() {
        return telefon;
    }
}
