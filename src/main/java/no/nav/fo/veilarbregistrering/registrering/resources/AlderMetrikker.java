package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.registrering.bruker.FnrUtils.utledAlderForFnr;

class AlderMetrikker {
    static void rapporterAlder(String fnr) {
        Event event = MetricsFactory.createEvent("registrering.bruker.alder");
        event.addFieldToReport("alder", utledAlderForFnr(fnr, now()));
        event.report();
    }
}
