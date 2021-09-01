package no.nav.fo.veilarbregistrering.besvarelse

import no.nav.fo.veilarbregistrering.metrics.Metric

enum class FremtidigSituasjonSvar : Metric {
    SAMME_ARBEIDSGIVER,
    SAMME_ARBEIDSGIVER_NY_STILLING,
    NY_ARBEIDSGIVER,
    USIKKER,
    INGEN_PASSER;

    override fun fieldName(): String = "fremtidigsituasjon"

    override fun value(): String = this.toString()
}