package no.nav.fo.veilarbregistrering.registrering.bruker

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.Metric.Companion.of
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService

internal object OrdinaerBrukerRegistreringMetrikker {
    @JvmStatic
    fun rapporterInvalidRegistrering(
            influxMetricsService: InfluxMetricsService,
            ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering
    ) {
        influxMetricsService.reportFields(
            Events.INVALID_REGISTRERING_EVENT,
            of("registrering", toJson(ordinaerBrukerRegistrering.getBesvarelse())),
            of("stilling", toJson(ordinaerBrukerRegistrering.getSisteStilling()))
        )
    }

    private fun toJson(obj: Any): String =
        try {
            ObjectMapper().writeValueAsString(obj)
        } catch (ignored: JsonProcessingException) {
            ""
        }
}