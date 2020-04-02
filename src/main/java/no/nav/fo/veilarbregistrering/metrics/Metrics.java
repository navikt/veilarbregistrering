package no.nav.fo.veilarbregistrering.metrics;

import no.nav.metrics.MetricsFactory;

import java.util.Arrays;
import java.util.Objects;

public class Metrics {

    public static void reportSimple(Event event, Metric field, Metric tag) {
        no.nav.metrics.Event metricsEvent = MetricsFactory.createEvent(event.name);
        metricsEvent.addFieldToReport(field.fieldName(), field.value());
        metricsEvent.addTagToReport(tag.fieldName(), tag.value());
        metricsEvent.report();
    }

    public static void reportTags(Event event, Metric... metric) {
        no.nav.metrics.Event metricsEvent = MetricsFactory.createEvent(event.name);
        Arrays.stream(metric)
                .filter(Objects::isNull)
                .forEach(m -> metricsEvent.addTagToReport(m.fieldName(), m.value()));
        metricsEvent.report();
    }

    public static void reportTags(Event event, HasMetrics hasMetrics, Metric... metric) {
        no.nav.metrics.Event metricsEvent = MetricsFactory.createEvent(event.name);
        hasMetrics.metrics().stream()
                .filter(Objects::nonNull)
                .forEach(m -> metricsEvent.addTagToReport(m.fieldName(), m.value()));
        Arrays.stream(metric)
                .filter(Objects::nonNull)
                .forEach(m -> metricsEvent.addTagToReport(m.fieldName(), m.value()));
        metricsEvent.report();
    }

    public static void reportFields(Event event, Metric... metric) {
        no.nav.metrics.Event metricsEvent = MetricsFactory.createEvent(event.name);
        Arrays.stream(metric)
                .filter(Objects::nonNull)
                .forEach(m -> metricsEvent.addFieldToReport(m.fieldName(), m.value()));
        metricsEvent.report();
    }

    public static void reportFields(Event event, HasMetrics hasMetrics, Metric... metric) {
        no.nav.metrics.Event metricsEvent = MetricsFactory.createEvent(event.name);
        hasMetrics.metrics().stream()
                .filter(Objects::nonNull)
                .forEach(m -> metricsEvent.addFieldToReport(m.fieldName(), m.value()));
        Arrays.stream(metric)
                .filter(Objects::nonNull)
                .forEach(m -> metricsEvent.addFieldToReport(m.fieldName(), m.value()));
        metricsEvent.report();
    }

    public enum Event {
        OPPGAVE_OPPRETTET_EVENT("arbeid.registrert.oppgave"),
        OPPGAVE_ALLEREDE_OPPRETTET_EVENT("arbeid.registrert.oppgave.allerede-opprettet"),
        START_REGISTRERING_EVENT("arbeid.registrering.start"),
        MANUELL_REGISTRERING_EVENT("registrering.manuell-registrering"),
        MANUELL_REAKTIVERING_EVENT("registrering.manuell-reaktivering"),
        SYKMELDT_BESVARELSE_EVENT("registrering.sykmeldt.besvarelse"),
        PROFILERING_EVENT("registrering.bruker.profilering"),
        BRUKER_ALDER_EVENT("registrering.bruker.alder"),
        INVALID_REGISTRERING_EVENT("registrering.invalid.registrering"),
        MAKSDATO_EVENT("registrering.maksdato");

        private final String name;

        Event(String name) {
            this.name = name;
        }
    }
}
