package no.nav.fo.veilarbregistrering.metrics

import io.prometheus.client.Gauge

enum class Gauges(override val gauge: Gauge): GaugeMetric {
    PLANLAGT_NEDETID_ARENA(Gauge.build().name("registrering.nedetid.arena").register());
}

interface GaugeMetric {
    val gauge: Gauge
}