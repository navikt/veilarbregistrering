package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.metrics.Metric

enum class RegistreringType : Metric {
    REAKTIVERING, ALLEREDE_REGISTRERT, SYKMELDT_REGISTRERING, ORDINAER_REGISTRERING;

    override fun fieldName() = "registreringType"
    override fun value() = this.toString()
}