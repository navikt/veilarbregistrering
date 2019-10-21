package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

class ProfileringMetrikker {
    static void rapporterProfilering(Profilering profilering) {
        Event event = MetricsFactory.createEvent("registrering.bruker.profilering");
        event.addFieldToReport("innsatsgruppe", profilering.getInnsatsgruppe());
        event.report();
    }
}
