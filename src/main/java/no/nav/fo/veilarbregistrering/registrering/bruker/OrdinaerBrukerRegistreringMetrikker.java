package no.nav.fo.veilarbregistrering.registrering.bruker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.fo.veilarbregistrering.metrics.Events;
import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.metrics.MetricsService;

class OrdinaerBrukerRegistreringMetrikker {
    static void rapporterInvalidRegistrering(MetricsService metricsService, OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        metricsService.reportFields(Events.INVALID_REGISTRERING_EVENT,
                Metric.of("registrering", toJson(ordinaerBrukerRegistrering.getBesvarelse())),
                Metric.of("stilling", toJson(ordinaerBrukerRegistrering.getSisteStilling())));
    }

    private static String toJson(Object obj) {
        String json = "";
        try {
            json = (new ObjectMapper()).writeValueAsString(obj);
        } catch (JsonProcessingException ignored) {
        }
        return json;
    }
}
