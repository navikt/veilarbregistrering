package no.nav.fo.veilarbregistrering.metrics

interface HasMetrics {
    fun metrics(): List<Metric>
}