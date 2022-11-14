package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.metrics.Metric

/**
 * Formidlingsgruppe:
 *
 * ARBS - Arbeidssøker
 * IARBS - Ikke arbeidssøker
 * ISERV - Ikke servicebruker
 */
data class Formidlingsgruppe(val kode: String): Metric {

    override fun fieldName() = "formidlingsgruppe"
    override fun value() = kode

    override fun toString(): String = "{kode='$kode'}"

    fun erArbeidssoker(): Boolean {
        return "ARBS" == kode
    }
}