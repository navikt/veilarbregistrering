package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.metrics.Event;
import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import static java.time.LocalDate.now;

class AlderMetrikker {
    static void rapporterAlder(InfluxMetricsService influxMetricsService, Foedselsnummer fnr) {
        influxMetricsService.reportFields(Event.of("registrering.bruker.alder"),
                Metric.of("alder", fnr.alder(now())));
    }
}
