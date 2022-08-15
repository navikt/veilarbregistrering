package no.nav.fo.veilarbregistrering.registrering

import no.nav.fo.veilarbregistrering.metrics.Metric

enum class Tilstandsfeil : Metric {

    ALLEREDE_UNDER_OPPFOLGING,
    IKKE_ORDINAER_REGISTRERING,
    IKKE_SYKEMELDT_REGISTRERING,
    KAN_IKKE_REAKTIVERES;

    override fun fieldName(): String {
        return "type"
    }

    override fun value(): Any {
        return this.name
    }
}