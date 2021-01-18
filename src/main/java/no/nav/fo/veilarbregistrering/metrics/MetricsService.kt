package no.nav.fo.veilarbregistrering.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import no.nav.common.metrics.MetricsClient
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import no.nav.common.metrics.Event as MetricsEvent

open class MetricsService(
    private val metricsClient: MetricsClient,
    private val meterRegistry: MeterRegistry
) {

    fun reportSimple(event: Event, field: Metric, tag: Metric) {
        val metricsEvent = MetricsEvent(event.key)
        metricsEvent.addFieldToReport(field.fieldName(), field.value())
        metricsEvent.addTagToReport(tag.fieldName(), tag.value().toString())
        metricsClient.report(metricsEvent)
    }

    fun reportTags(event: Event, vararg metrics: Metric): Unit =
        MetricsEvent(event.key)
            .also { addAllTags(it, metrics.toList()) }
            .let { metricsClient.report(it) }

    fun reportTags(event: Event, hasMetrics: HasMetrics, vararg metrics: Metric): Unit =
        MetricsEvent(event.key)
            .also { addAllTags(it, hasMetrics.metrics()) }
            .also { addAllTags(it, metrics.toList()) }
            .let { metricsClient.report(it) }

    fun reportFields(event: Event, vararg metrics: Metric) =
        MetricsEvent(event.key)
            .also { addAllFields(it, metrics.toList()) }
            .let { metricsClient.report(it) }

    fun reportFields(event: Event, hasMetrics: HasMetrics, vararg metrics: Metric) =
        MetricsEvent(event.key)
            .also { addAllFields(it, hasMetrics.metrics()) }
            .also { addAllFields(it, metrics.toList()) }
            .let { metricsClient.report(it) }

    fun reportTimer(event: Event, start: StartTime, failureCause: String? = null) {
        MetricsEvent("${event.key}.timer")
            .also { metricsEvent ->
                metricsEvent.addFieldToReport("value", System.nanoTime() - start.time)
                failureCause?.let { metricsEvent.addFieldToReport("aarsak", failureCause) }
            }
            .let { metricsClient.report(it) }
    }

    fun startTime() = StartTime(System.nanoTime())
    private val statusVerdier: Map<Status, AtomicInteger> = HashMap()

    fun rapporterRegistreringStatusAntall(status: Status, antall: Int) {
        val registrertAntall = statusVerdier.getOrElse(status) {
            val atomiskAntall = AtomicInteger()
            meterRegistry.gauge(
                "veilarbregistrering_registrert_status",
                listOf(Tag.of("status", status.name)),
                atomiskAntall
            ) { obj: AtomicInteger -> obj.get().toDouble() }
            atomiskAntall
        }
        registrertAntall.set(antall)
    }

    private fun addAllTags(event: MetricsEvent, metrics: List<Metric?>) =
        metrics.filterNotNull()
            .forEach { m -> event.addTagToReport(m.fieldName(), m.value().toString()) }

    private fun addAllFields(event: MetricsEvent, metrics: List<Metric?>) =
        metrics.filterNotNull()
            .forEach { m -> event.addFieldToReport(m.fieldName(), m.value().toString()) }

    class StartTime internal constructor(val time: Long)
}

enum class JaNei : Metric {
    JA, NEI;

    override fun fieldName(): String {
        return "svar"
    }

    override fun value(): Any {
        return this.toString()
    }
}

