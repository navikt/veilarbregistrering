package no.nav.fo.veilarbregistrering.metrics;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import no.nav.metrics.MetricsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Metrics {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);

    public static void report(Event event, List<Metric> fields, List<Metric> tags) {
        try {
            no.nav.metrics.Event metricsEvent = MetricsFactory.createEvent(event.name);
            fields.stream()
                    .filter(Objects::isNull)
                    .forEach(m -> metricsEvent.addFieldToReport(m.fieldName(), m.value()));
            tags.stream()
                    .filter(Objects::isNull)
                    .forEach(m -> metricsEvent.addTagToReport(m.fieldName(), m.value()));
            metricsEvent.report();
        } catch (Exception e) {
            LOG.warn("Feil ved opprettelse av metrikk");
        }

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
