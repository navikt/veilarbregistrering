package no.nav.fo.veilarbregistrering.db.arbeidssoker

import java.sql.Timestamp

internal class Formidlingsgruppeendring(
    val formidlingsgruppe: String,
    val personId: Int,
    val personIdStatus: String,
    val formidlingsgruppeEndret: Timestamp
) {

    fun erISERV(): Boolean {
        return formidlingsgruppe == "ISERV"
    }

    fun erAktiv(): Boolean {
        return personIdStatus == "AKTIV"
    }

    override fun toString(): String {
        return "Formidlingsgruppeendring{" +
                "formidlingsgruppe='" + formidlingsgruppe + '\'' +
                ", personId=" + personId +
                ", personIdStatus='" + personIdStatus + '\'' +
                ", formidlingsgruppeEndret=" + formidlingsgruppeEndret +
                '}'
    }
}