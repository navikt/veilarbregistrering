package no.nav.fo.veilarbregistrering.metrics;

import no.nav.metrics.MetricsFactory;

import java.util.Arrays;

public class Metrics {

    public static void report(Event event, Metric... metric) {
        no.nav.metrics.Event metricsEvent = MetricsFactory.createEvent(event.name);
        Arrays.stream(metric).forEach(m -> metricsEvent.addTagToReport(m.fieldName(), m.value()));
        metricsEvent.report();
    }

    public static void report(Event event, HasMetrics hasMetrics, Metric... metric) {
        no.nav.metrics.Event metricsEvent = MetricsFactory.createEvent(event.name);
        hasMetrics.metrics().stream().forEach(m -> metricsEvent.addTagToReport(m.fieldName(), m.value()));
        Arrays.stream(metric).forEach(m -> metricsEvent.addTagToReport(m.fieldName(), m.value()));
        metricsEvent.report();
    }

    public enum Event {
        OPPGAVE_OPPRETTET_EVENT("arbeid.registrert.oppgave"),
        START_REGISTRERING_EVENT("arbeid.registrering.start"),
        PROFILERING_EVENT("registrering.bruker.profilering"),
        INVALID_REGISTRERING_EVENT("registrering.invalid.registrering");

        private final String name;

        Event(String name) {
            this.name = name;
        }
    }
}
