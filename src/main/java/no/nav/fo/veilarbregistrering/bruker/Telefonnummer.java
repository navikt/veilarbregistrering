package no.nav.fo.veilarbregistrering.bruker;

public class Telefonnummer {

    private String nummer;
    private String landskode;

    public static Telefonnummer of(String nummer, String landskode) {
        return new Telefonnummer(nummer, landskode);
    }

    private Telefonnummer(String nummer, String landskode) {
        this.nummer = nummer;
        this.landskode = landskode;
    }

    /**
     * Returnerer telefonnummer på formatet "00xx xxxxxxxxx" hvis landkode er oppgitt.
     * Hvis ikke returneres bare xxxxxxxxx.
     */
    public String asLandkodeOgNummer() {
        return landskode != null ? landskode + " " + nummer : nummer;
    }
}
