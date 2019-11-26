package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

class BrukerRegistreringTypeMetrikker {
    static void rapporterManuellRegistrering(BrukerRegistreringType type){
        Event event = MetricsFactory.createEvent("registrering.manuell-registrering");
        event.addFieldToReport("type",  type.toString());
        event.report();
    }
}
