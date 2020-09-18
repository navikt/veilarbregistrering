package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metric;

public enum GeografikTilknytningAvstemning implements Metric {

    LIK,
    ULIK;

    @Override
    public String fieldName() {
        return "avstemning";
    }

    @Override
    public String value() {
        return this.toString();
    }
}
