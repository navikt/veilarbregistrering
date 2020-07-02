package no.nav.fo.veilarbregistrering.orgenhet;

import java.util.Objects;

public class Enhetsnr {

    private final String enhetId;

    public static Enhetsnr of(String enhetId) {
        return new Enhetsnr(enhetId);
    }

    private Enhetsnr(String enhetId) {
        this.enhetId = enhetId;
    }

    public String asString() {
        return enhetId;
    }

    public static Enhetsnr internBrukerstotte() {
        return Enhetsnr.of("2930");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enhetsnr enhetsnr = (Enhetsnr) o;
        return Objects.equals(enhetId, enhetsnr.enhetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enhetId);
    }

    @Override
    public String toString() {
        return "{" +
                "enhetId='" + enhetId + '\'' +
                '}';
    }
}
