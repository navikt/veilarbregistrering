package no.nav.fo.veilarbregistrering.kafka

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.EndretFormidlingsgruppeCommand
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.Operation
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

class FormidlingsgruppeEvent(
    override val foedselsnummer: Foedselsnummer,
    override val personId: String,
    override val personIdStatus: String,
    override val operation: Operation,
    override val formidlingsgruppe: Formidlingsgruppe,
    override val formidlingsgruppeEndret: LocalDateTime,
    override val forrigeFormidlingsgruppe: Formidlingsgruppe?,
    override val forrigeFormidlingsgruppeEndret: LocalDateTime?
) : EndretFormidlingsgruppeCommand {

    override fun toString(): String {
        return "FormidlingsgruppeEvent{" +
                "foedselsnummer=" + foedselsnummer.maskert() +
                ", personId='" + personId + '\'' +
                ", personIdStatus='" + personIdStatus + '\'' +
                ", operation='" + operation + '\'' +
                ", formidlingsgruppe=" + formidlingsgruppe +
                ", formidlingsgruppeEndret=" + formidlingsgruppeEndret.toString() +
                ", forrigeFormidlingsgruppe=" + forrigeFormidlingsgruppe?.toString() +
                ", forrigeFormidlingsgruppeEndret=" + forrigeFormidlingsgruppeEndret?.toString() +
                "'}'"
    }
}