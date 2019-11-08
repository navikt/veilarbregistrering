package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

public class GeografiskTilknytningMetrikker {

    static void rapporter(GeografiskTilknytning geografiskTilknytning, String formidlingsgruppe, RegistreringType registreringType) {
        Event event = MetricsFactory.createEvent("arbeid.registrering.start");
        event.addTagToReport(geografiskTilknytning.fieldName(), geografiskTilknytning.value());
        event.addTagToReport("formidlingsgruppe", "".equals(formidlingsgruppe) ? "INGEN_VERDI" : formidlingsgruppe);
        event.addTagToReport("registreringType", registreringType.toString());
        event.report();
    }
}
