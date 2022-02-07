package no.nav.fo.veilarbregistrering.registrering

import no.nav.fo.veilarbregistrering.metrics.Metric

enum class BrukerRegistreringType : Metric {
    ORDINAER, SYKMELDT;

    override fun fieldName(): String {
        return "type"
    }

    override fun value(): String {
        return this.toString()
    }
}