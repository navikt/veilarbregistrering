package no.nav.fo.veilarbregistrering.oppfolging

import no.nav.fo.veilarbregistrering.metrics.Metric

/**
 * Rettighetsgruppe beskriver "ytelsen" du har i Arena, type Dagpenger, AAP, osv
 */
data class Rettighetsgruppe(val kode: String): Metric {

    override fun fieldName() = "rettighetsgruppe"
    override fun value() = kode

    override fun toString(): String = "rettighetsgruppe='$kode'"
}