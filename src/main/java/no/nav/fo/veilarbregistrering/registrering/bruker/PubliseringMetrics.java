package no.nav.fo.veilarbregistrering.registrering.bruker;

import io.micrometer.core.instrument.Tag;
import no.nav.metrics.MetricsFactory;

import java.util.Arrays;

public class PubliseringMetrics {

    static void rapporterRegistreringStatusAntall(Status status, int antall) {
        MetricsFactory.getMeterRegistry().gauge("veilarbregistrering_registrert_status",
                Arrays.asList(Tag.of("status", status.name())),
                antall
        );

    }
}
