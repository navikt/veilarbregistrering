package no.nav.fo.veilarbregistrering.oppfolging

data class Servicegruppe (val servicegruppe: String) {

    override fun toString(): String {
        return "servicegruppe='$servicegruppe'"
    }
}