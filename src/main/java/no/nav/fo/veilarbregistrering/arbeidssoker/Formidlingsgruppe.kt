package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.metrics.Metric

/**
 * Formidlingsgruppe:
 *
 * ARBS - Arbeidssøker
 * IARBS - Ikke arbeidssøker
 * ISERV - Ikke servicebruker
 */
data class Formidlingsgruppe (private val formidlingsgruppe: String) : Metric {
    override fun fieldName(): String = "formidlingsgruppe"

    override fun value(): String = formidlingsgruppe

    fun stringValue(): String = formidlingsgruppe

    override fun toString(): String = "{kode='$formidlingsgruppe'}"

    fun erArbeidssoker(): Boolean {
        return "ARBS" == formidlingsgruppe
    }

    companion object {
        @JvmStatic
        fun of(formidlingsgruppe: String) : Formidlingsgruppe =
            Formidlingsgruppe(formidlingsgruppe)
    }
}