package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.metrics.Metric

enum class OppgaveType : Metric {
    OPPHOLDSTILLATELSE, UTVANDRET;

    override fun fieldName(): String {
        return "oppgavetype"
    }

    override fun value(): String {
        return this.toString()
    }
}