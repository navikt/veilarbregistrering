package no.nav.fo.veilarbregistrering.arbeidssoker

/**
 * Formidlingsgruppe:
 *
 * ARBS - Arbeidssøker
 * IARBS - Ikke arbeidssøker
 * ISERV - Ikke servicebruker
 */
data class Formidlingsgruppe(val kode: String) {

    override fun toString(): String = "{kode='$kode'}"

    fun erArbeidssoker(): Boolean {
        return "ARBS" == kode
    }
}