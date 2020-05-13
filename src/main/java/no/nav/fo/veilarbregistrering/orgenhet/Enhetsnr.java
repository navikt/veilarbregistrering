package no.nav.fo.veilarbregistrering.orgenhet;

public class Enhetsnr {

    private final int enhetId;

    public static Enhetsnr of(int enhetId) {
        return new Enhetsnr(enhetId);
    }

    private Enhetsnr(int enhetId) {
        this.enhetId = enhetId;
    }
}
