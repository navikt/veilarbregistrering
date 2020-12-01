package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

import static java.time.LocalDate.now;

class AlderMetrikker {
    static void rapporterAlder(Foedselsnummer fnr) {
        Event event = MetricsFactory.createEvent("registrering.bruker.alder");
        event.addFieldToReport("alder", fnr.alder(now()));
        event.report();
    }
}
