package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

public class GeografiskTilknytningMetrikker {

    static void rapporter(GeografiskTilknytning geografiskTilknytning) {
        Event event = MetricsFactory.createEvent("registrering.bruker.registrering.geografiskTilknytning");
        event.addFieldToReport(geografiskTilknytning.fiedldName(), geografiskTilknytning.value());
        event.report();
    }
}
