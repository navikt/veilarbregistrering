package no.nav.fo.veilarbregistrering.oppfolging

import no.nav.fo.veilarbregistrering.metrics.Metric
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import java.lang.IllegalArgumentException

enum class AktiverBrukerFeil: Metric {
    BRUKER_ER_UKJENT,
    BRUKER_KAN_IKKE_REAKTIVERES,
    BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET,
    BRUKER_MANGLER_ARBEIDSTILLATELSE, ;

    override fun fieldName() = "aarsak"
    override fun value() = this.toString()

    companion object {
        fun fromStatus(status: Status): AktiverBrukerFeil =
                when (status) {
                    Status.MANGLER_ARBEIDSTILLATELSE -> BRUKER_MANGLER_ARBEIDSTILLATELSE
                    Status.DOD_UTVANDRET_ELLER_FORSVUNNET -> BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET
                    Status.KAN_IKKE_REAKTIVERES -> BRUKER_KAN_IKKE_REAKTIVERES
                    Status.UKJENT_BRUKER -> BRUKER_ER_UKJENT
                    else -> throw IllegalArgumentException("Mottok en status som ikke er en feil til mapping")
                }
    }
}