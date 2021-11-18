package no.nav.fo.veilarbregistrering.enhet

import java.util.*

class Organisasjonsdetaljer (
    val forretningsadresser: List<Forretningsadresse> = emptyList(),
    val postadresser: List<Postadresse> = emptyList()
) {

    fun kommunenummer(): Optional<Kommune> {
        val kommunenummerFraForretningsadresse = kommunenummerFraFoersteGyldigeAdresse(forretningsadresser)
        return if (kommunenummerFraForretningsadresse.isPresent) {
            kommunenummerFraForretningsadresse
        } else kommunenummerFraFoersteGyldigeAdresse(postadresser)
    }

    private fun kommunenummerFraFoersteGyldigeAdresse(adresse: List<Adresse>): Optional<Kommune> {
        return adresse.stream()
            .filter { obj: Adresse -> obj.erGyldig() }
            .findFirst()
            .map(Adresse::kommunenummer)
    }

    companion object {
        fun of(
            forretningsadresser: List<Forretningsadresse> = emptyList(),
            postadresser: List<Postadresse> = emptyList()
        ): Organisasjonsdetaljer {
            return Organisasjonsdetaljer(forretningsadresser, postadresser)
        }
    }

}