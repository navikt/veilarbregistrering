package no.nav.fo.veilarbregistrering.orgenhet;

import java.util.Objects;

/**
 * Alle enheter har en 4-sifret kode i Norg.
 */
public class Enhetnr {

    private final String enhetNr;

    public static Enhetnr of(String enhetId) {
        return new Enhetnr(enhetId);
    }

    private Enhetnr(String enhetNr) {
        this.enhetNr = enhetNr;
    }

    public String asString() {
        return enhetNr;
    }

    public static Enhetnr internBrukerstotte() {
        return Enhetnr.of("2930");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enhetnr enhetnr = (Enhetnr) o;
        return Objects.equals(enhetNr, enhetnr.enhetNr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enhetNr);
    }

    @Override
    public String toString() {
        return "{" +
                "enhetNr='" + enhetNr + '\'' +
                '}';
    }
}
