package no.nav.fo.veilarbregistrering.bruker

data class Bruker(
    val gjeldendeFoedselsnummer: Foedselsnummer,
    val aktorId: AktorId,
    val historiskeFoedselsnummer: List<Foedselsnummer> = emptyList()
) {
    /**
     * Returnerer en liste med fødselsnummer som inneholder både det som er gjeldende nå, og alle de historiske.
     */
    fun alleFoedselsnummer(): List<Foedselsnummer> = listOf(gjeldendeFoedselsnummer) + historiskeFoedselsnummer
}