package no.nav.fo.veilarbregistrering.oppfolging

data class Rettighetsgruppe(val rettighetsgruppe: String) {

    override fun toString(): String {
        return "rettighetsgruppe='$rettighetsgruppe'"
    }

    fun stringValue(): String {
        return rettighetsgruppe
    }
}