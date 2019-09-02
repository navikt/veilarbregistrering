package no.nav.fo.veilarbregistrering.registrering;


public enum BrukerRegistreringType {
    ORDINAER("ORDINAER"),
    SYKMELDT("SYKMELDT");

    private String brukerRegistreringType;

    BrukerRegistreringType(String brukerRegistreringType) {
        this.brukerRegistreringType = brukerRegistreringType;
    }

    @Override
    public String toString() {
        return brukerRegistreringType;
    }
}
