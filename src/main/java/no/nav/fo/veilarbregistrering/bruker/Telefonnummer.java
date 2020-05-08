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

    public String getNummer() {
        return nummer;
    }

    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    public String getLandskode() {
        return landskode;
    }

    public void setLandskode(String landskode) {
        this.landskode = landskode;
    }

}
