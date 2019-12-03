package no.nav.fo.veilarbregistrering.besvarelse;

import no.nav.fo.veilarbregistrering.metrics.Metric;

public enum UtdanningSvar implements Metric {
    INGEN_UTDANNING,
    GRUNNSKOLE,
    VIDEREGAENDE_GRUNNUTDANNING,
    VIDEREGAENDE_FAGBREV_SVENNEBREV,
    HOYERE_UTDANNING_1_TIL_4,
    HOYERE_UTDANNING_5_ELLER_MER,
    INGEN_SVAR;

    @Override
    public String fieldName() {
        return "utdanning";
    }

    @Override
    public String value() {
        return this.toString();
    }
}
