package no.nav.fo.veilarbregistrering.bruker;

import java.util.Objects;
import java.util.Optional;

public class Telefonnummer {

    private final String nummer;
    private String landskode;

    public static Telefonnummer of(String nummer, String landskode) {
        return new Telefonnummer(nummer, landskode);
    }

    public static Telefonnummer of(String nummer) {
        return new Telefonnummer(nummer);

    }
    private Telefonnummer(String nummer) {
        this.nummer = nummer;
    }

    private Telefonnummer(String nummer, String landskode) {
        this.nummer = nummer;
        this.landskode = landskode;
    }

    public Optional<String> getNummer() {
        return Optional.ofNullable(this.nummer);
    }

    /**
     * Returnerer telefonnummer på formatet "00xx xxxxxxxxx" hvis landkode er oppgitt.
     * Hvis ikke returneres bare xxxxxxxxx.
     */
    public String asLandkodeOgNummer() {
        return landskode != null ? landskode + " " + nummer : nummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Telefonnummer that = (Telefonnummer) o;
        return Objects.equals(nummer, that.nummer) && Objects.equals(landskode, that.landskode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nummer, landskode);
    }
}
