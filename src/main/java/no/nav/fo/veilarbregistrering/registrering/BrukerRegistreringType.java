package no.nav.fo.veilarbregistrering.registrering;


import no.nav.fo.veilarbregistrering.metrics.Metric;

public enum BrukerRegistreringType implements Metric {
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

    @Override
    public String fieldName() {
        return "type";
    }

    @Override
    public String value() {
        return this.toString();
    }
}
