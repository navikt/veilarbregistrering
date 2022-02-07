package no.nav.fo.veilarbregistrering.enhet

class Organisasjonsdetaljer(
    private val forretningsadresser: List<Forretningsadresse> = emptyList(),
    private val postadresser: List<Postadresse> = emptyList()
) {
    fun kommunenummer(): Kommune? =
        kommunenummerFraFoersteGyldigeAdresse(forretningsadresser)
            ?: kommunenummerFraFoersteGyldigeAdresse(postadresser)
}

private fun kommunenummerFraFoersteGyldigeAdresse(adresse: List<Adresse>): Kommune? =
    adresse.firstOrNull(Adresse::erGyldig)?.kommunenummer
