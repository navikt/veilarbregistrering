package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import no.nav.fo.veilarbregistrering.metrics.Event;
import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.metrics.MetricsService;
import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType;


class StartRegistreringStatusMetrikker {
    static void rapporterRegistreringsstatus(MetricsService metricsService, StartRegistreringStatusDto registreringStatus) {
        metricsService.reportFields(
                Event.of("registrering.bruker.data"),
                Metric.of("erAktivIArena", registreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT),
                Metric.of("kreverReaktivering", registreringStatus.getRegistreringType() == RegistreringType.REAKTIVERING),
                Metric.of("sykmeldUnder39uker", registreringStatus.getRegistreringType() == RegistreringType.SPERRET),
                Metric.of("sykmeldOver39uker", registreringStatus.getRegistreringType() == RegistreringType.SYKMELDT_REGISTRERING),
                Metric.of("jobbetSiste6av12Mnd", registreringStatus.getJobbetSeksAvTolvSisteManeder())
        );

    }
}