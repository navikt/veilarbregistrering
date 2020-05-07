package no.nav.fo.veilarbregistrering.bruker.pdl;

public class PdlTelefonnummer {

    private String nummer;
    private String landskode;

    public PdlTelefonnummer() {
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
