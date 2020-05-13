package no.nav.fo.veilarbregistrering.orgenhet;

import java.util.Objects;

public class Enhetsnr {

    private final int enhetId;

    public static Enhetsnr of(int enhetId) {
        return new Enhetsnr(enhetId);
    }

    private Enhetsnr(int enhetId) {
        this.enhetId = enhetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enhetsnr enhetsnr = (Enhetsnr) o;
        return enhetId == enhetsnr.enhetId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enhetId);
    }
}
