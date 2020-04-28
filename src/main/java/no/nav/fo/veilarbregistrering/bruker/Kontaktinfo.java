package no.nav.fo.veilarbregistrering.bruker;

public class Kontaktinfo {

    private final String epost;
    private final String telefon;

    public static Kontaktinfo of(String epost, String telefon) {
        return new Kontaktinfo(epost, telefon);
    }

    private Kontaktinfo(String epost, String telefon) {
        this.epost = epost;
        this.telefon = telefon;
    }

    public String getEpost() {
        return epost;
    }

    public String getTelefon() {
        return telefon;
    }
}
