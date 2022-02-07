package no.nav.fo.veilarbregistrering.orgenhet.adapter

data class RsArbeidsfordelingCriteriaDto(
    val geografiskOmraade: String,
    val oppgavetype: String,
    val tema: String
) {
    companion object {
        const val KONTAKT_BRUKER = "KONT_BRUK"
        const val OPPFOLGING = "OPP"
    }
}