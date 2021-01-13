package no.nav.fo.veilarbregistrering.feil

open class Feil(private val type: Type, melding: String = "") : RuntimeException(melding) {

    interface Type {
        val name: String
        val status: Int
    }
}

