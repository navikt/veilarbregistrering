package no.nav.fo.veilarbregistrering.besvarelse

import no.nav.fo.veilarbregistrering.metrics.Metric

enum class UtdanningSvar : Metric {
    INGEN_UTDANNING,
    GRUNNSKOLE,
    VIDEREGAENDE_GRUNNUTDANNING,
    VIDEREGAENDE_FAGBREV_SVENNEBREV,
    HOYERE_UTDANNING_1_TIL_4,
    HOYERE_UTDANNING_5_ELLER_MER,
    INGEN_SVAR;

    override fun fieldName(): String = "utdanning"

    override fun value(): String = this.toString()
}