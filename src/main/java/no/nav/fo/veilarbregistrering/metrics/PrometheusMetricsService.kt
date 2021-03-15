package no.nav.fo.veilarbregistrering.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status
import java.util.HashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * PrometheusMetricsService fungerer som en abstraksjon mot Prometheus, og tilbyr funksjoner
 * for å ...
 *
 * Prometheus benytter en pull-modell, hvor appen tilbyr et endepunkt for å hente data. Se application.yml.
 */
class PrometheusMetricsService(private val meterRegistry: MeterRegistry) {

    fun rapporterRegistreringStatusAntall(antallPerStatus: Map<Status, Int>) {
        antallPerStatus.forEach {
            val registrertAntall = statusVerdier.computeIfAbsent(it.key) { key ->
                val atomiskAntall = AtomicInteger()
                meterRegistry.gauge(
                        "veilarbregistrering_registrert_status",
                        listOf(Tag.of("status", key.name)),
                        atomiskAntall)
                atomiskAntall
            }
            registrertAntall.set(it.value)
        }
    }
    
    private val statusVerdier: MutableMap<Status, AtomicInteger> = HashMap()
}
