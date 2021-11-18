package no.nav.fo.veilarbregistrering.enhet

import no.nav.fo.veilarbregistrering.enhet.KommuneMedBydel.Companion.contains

data class Kommunenummer (val kommunenummer: String) {

    fun kommuneMedBydeler(): Boolean {
        return contains(kommunenummer)
    }

    companion object {
        fun of(kommunenummer: String): Kommunenummer {
            return Kommunenummer(kommunenummer)
        }

        internal fun of(kommuneMedBydel: KommuneMedBydel): Kommunenummer {
            return Kommunenummer(kommuneMedBydel.kommenummer)
        }
    }
}