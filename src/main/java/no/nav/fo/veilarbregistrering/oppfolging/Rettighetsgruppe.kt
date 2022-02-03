package no.nav.fo.veilarbregistrering.oppfolging

data class Rettighetsgruppe private constructor(val rettighetsgruppe: String) {

    fun stringValue(): String {
        return rettighetsgruppe
    }

    companion object {
        fun of(rettighetsgruppe: String): Rettighetsgruppe {
            return Rettighetsgruppe(rettighetsgruppe)
        }
    }
}