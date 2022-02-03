package no.nav.fo.veilarbregistrering.arbeidssoker

/**
 * Formidlingsgruppe:
 *
 * ARBS - Arbeidssøker
 * IARBS - Ikke arbeidssøker
 * ISERV - Ikke servicebruker
 */
data class Formidlingsgruppe(private val formidlingsgruppe: String) {

    fun stringValue(): String = formidlingsgruppe

    override fun toString(): String = "{kode='$formidlingsgruppe'}"

    fun erArbeidssoker(): Boolean {
        return "ARBS" == formidlingsgruppe
    }
}