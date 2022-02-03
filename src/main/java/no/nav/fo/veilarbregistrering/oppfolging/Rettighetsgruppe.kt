package no.nav.fo.veilarbregistrering.oppfolging

data class Rettighetsgruppe(val rettighetsgruppe: String) {

    fun stringValue(): String {
        return rettighetsgruppe
    }
}