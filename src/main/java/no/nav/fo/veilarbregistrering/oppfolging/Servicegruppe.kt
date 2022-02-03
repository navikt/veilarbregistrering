package no.nav.fo.veilarbregistrering.oppfolging

data class Servicegruppe (val servicegruppe: String) {

    fun stringValue(): String {
        return servicegruppe
    }
}