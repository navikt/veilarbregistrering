package no.nav.fo.veilarbregistrering.metrics

import no.nav.common.metrics.MetricsClient
import no.nav.common.metrics.Event as MetricsEvent

class MetricsService(private val metricsClient: MetricsClient) {

    fun reportSimple(event: Event, field: Metric, tag: Metric) {
        val metricsEvent = MetricsEvent(event.key)
        metricsEvent.addFieldToReport(field.fieldName(), field.value())
        metricsEvent.addTagToReport(tag.fieldName(), tag.value().toString())
        metricsClient.report(metricsEvent)
    }

    fun reportTags(event: Event, vararg metrics: Metric?): Unit =
        MetricsEvent(event.key)
            .also { addAllTags(it, metrics.toList()) }
            .let { metricsClient.report(it) }

    fun reportTags(event: Event, hasMetrics: HasMetrics, vararg metrics: Metric?): Unit =
        MetricsEvent(event.key)
            .also { addAllTags(it, hasMetrics.metrics()) }
            .also { addAllTags(it, metrics.toList()) }
            .let { metricsClient.report(it) }

    fun reportFields(event: Event, vararg metrics: Metric?) =
        MetricsEvent(event.key)
            .also { addAllFields(it, metrics.toList()) }
            .let { metricsClient.report(it) }

    fun reportFields(event: Event, hasMetrics: HasMetrics, vararg metrics: Metric?) =
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

    private fun addAllTags(event: MetricsEvent, metrics: List<Metric?>) =
        metrics.filterNotNull()
            .forEach { m -> event.addTagToReport(m.fieldName(), m.value().toString()) }

    private fun addAllFields(event: MetricsEvent, metrics: List<Metric?>) =
        metrics.filterNotNull()
            .forEach { m -> event.addFieldToReport(m.fieldName(), m.value().toString()) }
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

class StartTime(val time: Long)
