package no.nav.fo.veilarbregistrering.registrering.bruker

import io.micrometer.core.instrument.Tag
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDto

internal object StartRegistreringStatusMetrikker {
    fun rapporterRegistreringsstatus(prometheusMetricsService: PrometheusMetricsService, registreringStatus: StartRegistreringStatusDto) {
        val iArbeidSiste12Mnd = registreringStatus.jobbetSeksAvTolvSisteManeder ?: false
        val type = when (registreringStatus.registreringType) {
            RegistreringType.ALLEREDE_REGISTRERT -> "allerede_registrert"
            RegistreringType.REAKTIVERING -> "reaktivering"
            RegistreringType.SYKMELDT_REGISTRERING -> "sykmeldt"
            else -> "ordinaer"
        }

        prometheusMetricsService.registrer(Events.START_REGISTRERING_EVENT, Tag.of("type", type), Tag.of("iArbeidSiste12Mnd", iArbeidSiste12Mnd.toString()))
    }
}