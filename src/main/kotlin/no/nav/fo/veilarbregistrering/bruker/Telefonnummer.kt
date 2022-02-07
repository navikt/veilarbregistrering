package no.nav.fo.veilarbregistrering.bruker

import java.util.*

data class Telefonnummer (val nummer: String, private val landskode: String? = null) {

    /**
     * Returnerer telefonnummer på formatet "00xx xxxxxxxxx" hvis landkode er oppgitt.
     * Hvis ikke returneres bare xxxxxxxxx.
     */
    fun asLandkodeOgNummer(): String {
        return if (landskode != null) "$landskode $nummer" else nummer
    }

}