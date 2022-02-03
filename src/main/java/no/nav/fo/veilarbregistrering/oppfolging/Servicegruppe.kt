package no.nav.fo.veilarbregistrering.oppfolging

data class Servicegruppe (val servicegruppe: String) {

    fun stringValue(): String {
        return servicegruppe
    }

    companion object {
        fun of(servicegruppe: String): Servicegruppe {
            return Servicegruppe(servicegruppe)
        }
    }
}