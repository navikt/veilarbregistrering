package no.nav.fo.veilarbregistrering.db.arbeidssoker

import java.sql.Timestamp

internal class Formidlingsgruppeendring(
    val formidlingsgruppe: String,
    val personId: Int,
    val personIdStatus: String,
    val formidlingsgruppeEndret: Timestamp
) {
    fun erARBS(): Boolean {
        return formidlingsgruppe == "ARBS"
    }

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

    internal class NyesteFoerst : Comparator<Formidlingsgruppeendring> {
        override fun compare(t0: Formidlingsgruppeendring, t1: Formidlingsgruppeendring): Int {
            return t1.formidlingsgruppeEndret.compareTo(t0.formidlingsgruppeEndret)
        }

        companion object {
            fun nyesteFoerst(): NyesteFoerst {
                return NyesteFoerst()
            }
        }
    }
}