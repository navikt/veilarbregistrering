package no.nav.fo.veilarbregistrering.registrering.bruker;

import io.micrometer.core.instrument.Tag;
import no.nav.metrics.MetricsFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PubliseringMetrikker {

    private static final Map<Status, AtomicInteger> statusVerdier = new HashMap<>();

    public static void rapporterRegistreringStatusAntall(Status status, int antall) {
        AtomicInteger registrertAntall = statusVerdier.computeIfAbsent(status, (s) -> {
            AtomicInteger atomiskAntall = new AtomicInteger();
            MetricsFactory.getMeterRegistry().gauge("veilarbregistrering_registrert_status",
                    Arrays.asList(Tag.of("status", status.name())),
                    atomiskAntall, AtomicInteger::get
            );
            return atomiskAntall;
        });

        registrertAntall.set(antall);
    }
}
