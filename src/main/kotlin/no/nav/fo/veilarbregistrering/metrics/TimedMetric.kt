package no.nav.fo.veilarbregistrering.metrics

import java.time.Clock
import java.time.Duration
import java.time.Instant

abstract class TimedMetric(private val metricsService: PrometheusMetricsService) : Metric {

    override fun fieldName() = "tjeneste"

    fun <T> doTimedCall(event: Event? = null, httpCall: () -> T): T {
        val start = Instant.now(Clock.systemDefaultZone())
        val result = httpCall()
        val end = Instant.now(Clock.systemDefaultZone())
        metricsService.registrerTimer(Events.KALL_TREDJEPART, Duration.between(start, end), this)
        event?.let { metricsService.registrer(it) }
        return result
    }
}