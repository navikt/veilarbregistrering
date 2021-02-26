package no.nav.fo.veilarbregistrering.metrics

import no.nav.common.metrics.MetricsClient

/**
 * InfluxMetrisService fungerer som en abstraksjon mot Influx, og tilbyr funksjoner
 * for å rapportere ulike Events
 *
 * Influx benytter en push-modell, hvor appen pusher data.
 */
open class InfluxMetricsService(private val metricsClient: MetricsClient) {

    fun reportSimple(event: Event, field: Metric, tag: Metric) =
        report(event, mapOf(field.fieldName() to field.value()), mapOf(tag.fieldName() to tag.value().toString()))

    fun reportTags(event: Event, vararg metrics: Metric): Unit =
            report(event, mapOf(), metrics.map { it.fieldName() to it.value().toString() }.toMap())

    fun reportFields(event: Event, vararg metrics: Metric) =
            report(event, metrics.map { it.fieldName() to it.value() }.toMap(), mapOf())

    fun reportFields(event: Event, hasMetrics: HasMetrics, vararg metrics: Metric) {
        val allFields = hasMetrics.metrics()
            .map { it.fieldName() to it.value() }
            .toMap().toMutableMap()

        allFields.putAll(metrics.map { it.fieldName() to it.value() })

        report(event, allFields, mapOf())
    }

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
        val fields = mapOf("value" to (System.nanoTime() - start.time).toString()).toMutableMap()
        failureCause?.let { fields.put("aarsak", failureCause) }

        report("${event.key}.timer", fields, mutableMapOf())
    }

    class StartTime internal constructor(val time: Long)

    private fun report(event: Event, fields: Map<String, Any>, tags: Map<String, String>) =
        report(event.key, fields, tags)

    private fun report(eventKey: String, fields: Map<String, Any>, tags: Map<String, String>) {
        tags.toMutableMap().putIfAbsent("environemt", "q1")
        metricsClient.report(eventKey, fields.toMutableMap(), tags.toMutableMap(), System.currentTimeMillis())
    }
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

