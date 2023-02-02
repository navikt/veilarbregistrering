package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeEndret
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

data class FormidlingsgruppeEndretEvent(
    val foedselsnummer: Foedselsnummer,
    val personId: String,
    val personIdStatus: String,
    val operation: Operation,
    val formidlingsgruppe: Formidlingsgruppe,
    val formidlingsgruppeEndret: LocalDateTime,
    val forrigeFormidlingsgruppe: Formidlingsgruppe?,
    val forrigeFormidlingsgruppeEndret: LocalDateTime?
): FormidlingsgruppeEndret {
    override fun opprettetTidspunkt(): LocalDateTime = formidlingsgruppeEndret

    override fun formidlingsgruppe(): Formidlingsgruppe = formidlingsgruppe

    fun erISERV(): Boolean {
        return formidlingsgruppe.kode == "ISERV"
    }

    fun erAktiv(): Boolean {
        return personIdStatus == "AKTIV"
    }

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