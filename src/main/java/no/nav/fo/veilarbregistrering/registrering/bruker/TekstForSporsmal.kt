package no.nav.fo.veilarbregistrering.registrering.bruker

data class TekstForSporsmal(
    val sporsmalId: String,
    val sporsmal: String,
    val svar: String,
) {
    override fun toString(): String {
        return "TekstForSporsmal(sporsmalId=$sporsmalId, sporsmal=$sporsmal, svar=$svar)"
    }
}