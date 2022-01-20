package no.nav.fo.veilarbregistrering.registrering.bruker

import io.micrometer.core.instrument.Tag
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import java.time.LocalDate

object AlderMetrikker {
    @JvmStatic
    fun rapporterAlder(metricsService: PrometheusMetricsService, fnr: Foedselsnummer) {
        val aldersgruppe = when (fnr.alder(LocalDate.now())) {
            in 0..30 -> "ung"
            in 31..49 -> "kss"
            else -> "senior"
        }
        metricsService.registrer(
            Events.BESVARELSE_ALDER,
            Tag.of("aldresgruppe", aldersgruppe))
    }
}