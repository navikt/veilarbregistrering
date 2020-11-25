package no.nav.fo.veilarbregistrering.log;

import no.nav.common.utils.IdUtils;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;

import static no.nav.log.MDCConstants.MDC_CALL_ID;

public class CallId {

    public static byte[] getCorrelationIdAsBytes() {
        String correlationId = MDC.get(MDC_CALL_ID);

        if (correlationId == null) {
            correlationId = MDC.get("jobId");
        }

        if (correlationId == null) {
            correlationId = IdUtils.generateId();
        }

        return correlationId.getBytes(StandardCharsets.UTF_8);
    }

    public static void leggTilCallId() {
        if (MDC.get(MDC_CALL_ID) != null) {
            return;
        }

        MDC.put(MDC_CALL_ID, IdUtils.generateId());
    }
}
