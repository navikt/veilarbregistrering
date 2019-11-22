package no.nav.fo.veilarbregistrering.registrering.bruker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.INVALID_REGISTRERING_EVENT;

class OrdinaerBrukerRegistreringMetrikker {
    static void rapporterInvalidRegistrering(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        Event event = MetricsFactory.createEvent(INVALID_REGISTRERING_EVENT.name());
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
