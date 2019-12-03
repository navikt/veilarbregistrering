package no.nav.fo.veilarbregistrering.besvarelse;

import no.nav.fo.veilarbregistrering.metrics.Metric;

public enum FremtidigSituasjonSvar implements Metric {
    SAMME_ARBEIDSGIVER,
    SAMME_ARBEIDSGIVER_NY_STILLING,
    NY_ARBEIDSGIVER,
    USIKKER,
    INGEN_PASSER,
    ;

    @Override
    public String fieldName() {
        return "fremtidigsituasjon";
    }

    @Override
    public String value() {
        return this.toString();
    }
}
