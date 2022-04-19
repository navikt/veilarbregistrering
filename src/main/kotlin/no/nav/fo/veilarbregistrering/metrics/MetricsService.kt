package no.nav.fo.veilarbregistrering.metrics

import io.micrometer.core.instrument.Tag
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import java.time.Duration

interface MetricsService {
    fun rapporterRegistreringStatusAntall(antallPerStatus: Map<Status, Int>)
    fun registrer(event: Event, vararg metrikker: Metric)
    fun registrer(event: Event, vararg tags: Tag)
    fun registrer(event: Event)
    fun registrerTimer(event: Event, tid: Duration, vararg metrikker: Metric)
}