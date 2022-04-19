package no.nav.fo.veilarbregistrering.log

import org.slf4j.MDC
import no.nav.common.log.MDCConstants
import no.nav.common.utils.IdUtils
import java.nio.charset.StandardCharsets

object CallId {
    val correlationIdAsBytes: ByteArray
        get() {
            var correlationId = MDC.get(MDCConstants.MDC_CALL_ID)
            if (correlationId == null) {
                correlationId = MDC.get("jobId")
            }
            if (correlationId == null) {
                correlationId = IdUtils.generateId()
            }
            return correlationId!!.toByteArray(StandardCharsets.UTF_8)
        }

    fun leggTilCallId() {
        if (MDC.get(MDCConstants.MDC_CALL_ID) != null) {
            return
        }
        MDC.put(MDCConstants.MDC_CALL_ID, IdUtils.generateId())
    }
}