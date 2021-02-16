package no.nav.fo.veilarbregistrering.metrics

import no.nav.common.metrics.MetricsClient
import no.nav.common.metrics.Event as MetricsEvent

/**
 * InfluxMetrisService fungerer som en abstraksjon mot Influx, og tilbyr funksjoner
 * for å rapportere ulike Events
 *
 * Influx benytter en push-modell, hvor appen pusher data.
 */
open class InfluxMetricsService(private val metricsClient: MetricsClient) {

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

    private fun addAllTags(event: MetricsEvent, metrics: List<Metric?>) =
            metrics.filterNotNull()
                    .forEach { m -> event.addTagToReport(m.fieldName(), m.value().toString()) }

    fun reportFields(event: Event, vararg metrics: Metric) =
            MetricsEvent(event.key)
                    .also { addAllFields(it, metrics.toList()) }
                    .let { metricsClient.report(it) }

    fun reportFields(event: Event, hasMetrics: HasMetrics, vararg metrics: Metric) =
            MetricsEvent(event.key)
                    .also { addAllFields(it, hasMetrics.metrics()) }
                    .also { addAllFields(it, metrics.toList()) }
                    .let { metricsClient.report(it) }

    private fun addAllFields(event: MetricsEvent, metrics: List<Metric?>) =
            metrics.filterNotNull()
                    .forEach { m -> event.addFieldToReport(m.fieldName(), m.value().toString()) }

    inline fun <R> timeAndReport(metricName: Events, block: () -> R): R {
        val startTime = startTime()
        var result: R? = null
        var throwable: Throwable? = null

        try {
            result = block()
        } catch (t: Throwable) {
            throwable = t
        } finally {
            reportTimer(metricName, startTime, throwable?.message)
        }

        when (throwable) {
            null -> return result ?: throw IllegalStateException("Error in timing block")
            else -> throw throwable
        }
    }

    fun startTime() = StartTime(System.nanoTime())

    fun reportTimer(event: Event, start: StartTime, failureCause: String? = null) {
        MetricsEvent("${event.key}.timer")
                .also { metricsEvent ->
                    metricsEvent.addFieldToReport("value", System.nanoTime() - start.time)
                    failureCause?.let { metricsEvent.addFieldToReport("aarsak", failureCause) }
                }
                .let { metricsClient.report(it) }
    }

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

