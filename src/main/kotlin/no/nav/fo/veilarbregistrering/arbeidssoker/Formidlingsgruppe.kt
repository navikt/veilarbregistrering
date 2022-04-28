package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.metrics.Metric

/**
 * Formidlingsgruppe:
 *
 * ARBS - Arbeidssøker
 * IARBS - Ikke arbeidssøker
 * ISERV - Ikke servicebruker
 */
enum class Formidlingsgruppe(val kode: String): Metric {

    ARBEIDSSOKER("ARBS"),
    IKKE_ARBEIDSSØKER("IARBS"),
    IKKE_SERVICEBRUKER("ISERV");

    override fun fieldName() = "formidlingsgruppe"
    override fun value() = kode

    fun erArbeidssoker(): Boolean {
        return "ARBS" == kode
    }

    companion object {
        fun valueOfKode(kode: String): Formidlingsgruppe {
            if (kode.isNullOrEmpty()) {
                throw NullPointerException("Kode for formidlingsgruppe kan ikke være null eller tomt")
            }

            return values()
                .filter { it.kode.equals(kode) }
                .first()
        }
    }
}