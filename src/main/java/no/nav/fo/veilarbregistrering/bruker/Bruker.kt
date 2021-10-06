package no.nav.fo.veilarbregistrering.bruker

data class Bruker(
    val gjeldendeFoedselsnummer: Foedselsnummer,
    val aktorId: AktorId,
    val historiskeFoedselsnummer: List<Foedselsnummer> = emptyList()
) {
    fun alleFoedselsnummer(): List<Foedselsnummer> = listOf(gjeldendeFoedselsnummer) + historiskeFoedselsnummer

    companion object {
        @JvmStatic
        fun of(foedselsnummer: Foedselsnummer, aktorId: AktorId): Bruker {
            return Bruker(foedselsnummer, aktorId)
        }

        @JvmStatic
        fun of(
            gjeldendeFoedselsnummer: Foedselsnummer,
            gjeldendeAktorId: AktorId,
            historiskeFoedselsnummer: List<Foedselsnummer> = emptyList()
        ): Bruker {
            return Bruker(gjeldendeFoedselsnummer, gjeldendeAktorId, historiskeFoedselsnummer)
        }
    }
}