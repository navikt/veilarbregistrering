package no.nav.fo.veilarbregistrering.metrics

import no.nav.common.metrics.MetricsClient
import no.nav.common.metrics.SensuConfig
import no.nav.common.utils.EnvironmentUtils
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
        report(metricsEvent)
    }

    fun reportTags(event: Event, vararg metrics: Metric): Unit =
        MetricsEvent(event.key)
            .also { addAllTags(it, metrics.toList()) }
            .let(::report)

    private fun addAllTags(event: MetricsEvent, metrics: List<Metric?>) =
        metrics.filterNotNull()
            .forEach { m -> event.addTagToReport(m.fieldName(), m.value().toString()) }

    fun reportFields(event: Event, vararg metrics: Metric) =
        MetricsEvent(event.key)
            .also { addAllFields(it, metrics.toList()) }
            .let(::report)

    fun reportFields(event: Event, hasMetrics: HasMetrics, vararg metrics: Metric) =
        MetricsEvent(event.key)
            .also { addAllFields(it, hasMetrics.metrics()) }
            .also { addAllFields(it, metrics.toList()) }
            .let(::report)

    private fun addAllFields(event: MetricsEvent, metrics: List<Metric?>) =
        metrics.filterNotNull()
            .forEach { m -> event.addFieldToReport(m.fieldName(), m.value().toString()) }

    private fun report(event: MetricsEvent) {
        event.addTagToReport("environment", EnvironmentUtils.getRequiredProperty("APP_ENVIRONMENT_NAME"))
        event.addTagToReport("host", SensuConfig.defaultConfig().hostname)
        metricsClient.report(event)
    }
}



