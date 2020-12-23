package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.metrics.Event;
import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.metrics.MetricsService;
import static java.time.LocalDate.now;

class AlderMetrikker {
    static void rapporterAlder(MetricsService metricsService, Foedselsnummer fnr) {
        metricsService.reportFields(Event.of("registrering.bruker.alder"),
                Metric.of("alder", fnr.alder(now())));
    }
}
