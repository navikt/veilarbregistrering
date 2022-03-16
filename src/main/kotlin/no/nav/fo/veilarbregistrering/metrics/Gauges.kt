package no.nav.fo.veilarbregistrering.metrics

import io.prometheus.client.Gauge

enum class Gauges(override val gauge: Gauge): GaugeMetric {
    PLANLAGT_NEDETID_ARENA(Gauge.build().name("registrering_nedetid_arena").help("Planlagt nedetid i Arena").register());
}

interface GaugeMetric {
    val gauge: Gauge
}