package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.metrics.Event.Companion.of
import no.nav.fo.veilarbregistrering.metrics.Metric.Companion.of
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType

internal object StartRegistreringStatusMetrikker {
    @JvmStatic
    fun rapporterRegistreringsstatus(metricsService: MetricsService, registreringStatus: StartRegistreringStatusDto) {
        var fields = listOf(
            of("erAktivIArena", registreringStatus.registreringType == RegistreringType.ALLEREDE_REGISTRERT),
            of("kreverReaktivering", registreringStatus.registreringType == RegistreringType.REAKTIVERING),
            of("sykmeldUnder39uker", registreringStatus.registreringType == RegistreringType.SPERRET),
            of("sykmeldOver39uker", registreringStatus.registreringType == RegistreringType.SYKMELDT_REGISTRERING)
        )

        registreringStatus.jobbetSeksAvTolvSisteManeder?.let {
            fields = fields + of("jobbetSiste6av12Mnd", it)
        }

        metricsService.reportFields(
            of("registrering.bruker.data"),
            *fields.toTypedArray()
        )
    }
}