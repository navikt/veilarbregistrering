package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.registrering.bruker.FnrUtils.utledAlderForFnr;

class AlderMetrikker {
    static void rapporterAlder(Foedselsnummer fnr) {
        Event event = MetricsFactory.createEvent("registrering.bruker.alder");
        event.addFieldToReport("alder", utledAlderForFnr(fnr.stringValue(), now())); //TODO: Flytt logikk til Foedselsnummer
        event.report();
    }
}
