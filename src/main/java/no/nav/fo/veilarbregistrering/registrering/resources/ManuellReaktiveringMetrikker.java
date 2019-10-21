package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

class ManuellReaktiveringMetrikker {
    static void rapporterManuellReaktivering(){
        Event event = MetricsFactory.createEvent("registrering.manuell-reaktivering");
        event.report();
    }
}
