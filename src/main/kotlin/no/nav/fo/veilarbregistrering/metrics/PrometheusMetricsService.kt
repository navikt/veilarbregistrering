package no.nav.fo.veilarbregistrering.metrics

import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * PrometheusMetricsService fungerer som en abstraksjon mot Prometheus, og tilbyr funksjoner
 * for å ...
 *
 * Prometheus benytter en pull-modell, hvor appen tilbyr et endepunkt for å hente data. Se application.yml.
 */
class PrometheusMetricsService : MetricsService {

    override fun rapporterRegistreringStatusAntall(antallPerStatus: Map<Status, Int>) {
        antallPerStatus.forEach {
            val registrertAntall = statusVerdier.computeIfAbsent(it.key) { key ->
                val atomiskAntall = AtomicInteger()
                Metrics.gauge(
                        "veilarbregistrering_registrert_status",
                        listOf(Tag.of("status", key.name)),
                        atomiskAntall)
                atomiskAntall
            }
            registrertAntall.set(it.value)
        }
    }
    
    private val statusVerdier: MutableMap<Status, AtomicInteger> = EnumMap(Status::class.java)

    override fun registrer(event: Event, vararg metrikker: Metric) {
        val tags = metrikker.map { Tag.of(it.fieldName(), it.value().toString()) }.toTypedArray()
        registrer(event, *tags)
    }

    override fun registrer(event: Event, vararg tags: Tag) {
        Metrics.counter(event.key, tags.asIterable()).increment()
    }

    override fun registrer(event: Event) {
        Metrics.counter(event.key).increment()
    }

    override fun registrerTimer(event: Event, tid: Duration, vararg metrikker: Metric) {
        val tags = metrikker.map { Tag.of(it.fieldName(), it.value().toString()) }.toTypedArray()
        registrerTimer(event, tid, *tags)
    }

    private fun registrerTimer(event: Event, tid: Duration, vararg tags: Tag) {
        Metrics.timer(event.key, tags.asIterable()).record(tid)
    }
}
