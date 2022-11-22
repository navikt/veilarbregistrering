package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.audit_log.cef.CefMessage
import no.nav.common.audit_log.cef.CefMessageEvent
import no.nav.common.audit_log.cef.CefMessageSeverity
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

class CefMelding(private val melding: String, private val foedselsnummer: Foedselsnummer) {

    fun cefMessage() : String {
        val cefMessage = CefMessage.builder()
            .applicationName("veilarbregistrering")
            .event(CefMessageEvent.ACCESS)
            .name("Sporingslogg")
            .severity(CefMessageSeverity.INFO)
            .destinationUserId(foedselsnummer.foedselsnummer)
            .timeEnded(System.currentTimeMillis())
            .extension("msg", melding)
            .build()

        return cefMessage.toString()
    }
}