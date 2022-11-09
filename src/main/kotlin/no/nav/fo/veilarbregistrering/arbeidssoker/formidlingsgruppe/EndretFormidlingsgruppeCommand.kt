package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.fo.veilarbregistrering.arbeidssoker.Operation
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

interface EndretFormidlingsgruppeCommand {
    val foedselsnummer: Foedselsnummer?
    val personId: String
    val personIdStatus: String?
    val operation: Operation
    val formidlingsgruppe: Formidlingsgruppe
    val formidlingsgruppeEndret: LocalDateTime
    val forrigeFormidlingsgruppe: Formidlingsgruppe?
    val forrigeFormidlingsgruppeEndret: LocalDateTime?
}