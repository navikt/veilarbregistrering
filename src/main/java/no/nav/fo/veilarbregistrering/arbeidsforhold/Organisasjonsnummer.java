package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.util.Objects;

/**
 * Nisifret nummer som entydig identifiserer enheter i Enhetsregisteret.
 */
public class Organisasjonsnummer {

    private final String organisasjonsnummer;

    public static Organisasjonsnummer of(String organisasjonsnummer) {
        return new Organisasjonsnummer(organisasjonsnummer);
    }

    private Organisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    public String asString() {
        return organisasjonsnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organisasjonsnummer that = (Organisasjonsnummer) o;
        return Objects.equals(organisasjonsnummer, that.organisasjonsnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organisasjonsnummer);
    }
}
