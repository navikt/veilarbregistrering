package no.nav.fo.veilarbregistrering.oppfolging

/**
 * Rettighetsgruppe beskriver "ytelsen" du har i Arena, type Dagpenger, AAP, osv
 */
data class Rettighetsgruppe(val kode: String) {

    override fun toString(): String = "rettighetsgruppe='$kode'"
}