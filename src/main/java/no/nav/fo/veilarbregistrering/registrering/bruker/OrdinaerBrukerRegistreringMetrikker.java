package no.nav.fo.veilarbregistrering.registrering.bruker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

class OrdinaerBrukerRegistreringMetrikker {
    static void rapporterInvalidRegistrering(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        Event event = MetricsFactory.createEvent("registrering.invalid.registrering");
        event.addFieldToReport("registrering", toJson(ordinaerBrukerRegistrering.getBesvarelse()));
        event.addFieldToReport("stilling", toJson(ordinaerBrukerRegistrering.getSisteStilling()));
        event.report();
    }

    private static String toJson(Object obj) {
        String json = "";
        try {
            json = (new ObjectMapper()).writeValueAsString(obj);
        } catch (JsonProcessingException ignored) {  }
        return json;
    }
}
