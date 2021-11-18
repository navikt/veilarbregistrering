package no.nav.fo.veilarbregistrering.enhet

data class Kommunenummer (val kommunenummer: String) {

    fun kommuneMedBydeler(): Boolean = kommunenummer in KommuneMedBydel

    companion object {
        fun of(kommunenummer: String): Kommunenummer {
            return Kommunenummer(kommunenummer)
        }

        internal fun of(kommuneMedBydel: KommuneMedBydel): Kommunenummer {
            return Kommunenummer(kommuneMedBydel.kommenummer)
        }
    }
}