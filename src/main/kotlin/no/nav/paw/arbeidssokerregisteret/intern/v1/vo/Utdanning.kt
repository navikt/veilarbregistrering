package no.nav.paw.arbeidssokerregisteret.intern.v1.vo

data class Utdanning(
    val utdanningsnivaa: Utdanningsnivaa,
    val bestaatt: JaNeiVetIkke,
    val godkjent: JaNeiVetIkke,
)
