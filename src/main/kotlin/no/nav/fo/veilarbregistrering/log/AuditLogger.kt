package no.nav.fo.veilarbregistrering.log

import no.nav.common.audit_log.cef.CefMessage
import no.nav.common.audit_log.cef.CefMessageEvent
import no.nav.common.audit_log.cef.CefMessageSeverity
import no.nav.common.audit_log.log.AuditLoggerImpl
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

class AuditLogger {
    val auditLogger = AuditLoggerImpl()

    fun log(foedselsnummer: Foedselsnummer, melding: String) {
        val cefMessage = CefMessage.builder()
            .applicationName("veilarbregistrering")
            .event(CefMessageEvent.ACCESS)
            .name("Sporingslogg")
            .severity(CefMessageSeverity.INFO)
            .destinationUserId(foedselsnummer.foedselsnummer)
            .timeEnded(System.currentTimeMillis())
            .extension("msg", melding)
            .build()
        auditLogger.log(cefMessage)
    }
}
