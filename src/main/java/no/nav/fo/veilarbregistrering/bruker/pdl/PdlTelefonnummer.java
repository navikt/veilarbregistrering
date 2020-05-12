package no.nav.fo.veilarbregistrering.bruker.pdl;

public class PdlTelefonnummer implements Comparable {

    private String nummer;
    private String landskode;
    private int prioritet;

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

    public int getPrioritet() {
        return prioritet;
    }

    public void setPrioritet(int prioritet) {
        this.prioritet = prioritet;
    }

    @Override
    public int compareTo(Object o) {
        if (this.prioritet > ((PdlTelefonnummer)o).getPrioritet()) {
            return 1;
        }
        if (this.prioritet < ((PdlTelefonnummer)o).getPrioritet()) {
            return -1;
        }
        return 0;
    }
}
