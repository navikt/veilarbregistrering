package no.nav.fo.veilarbregistrering.arbeidsforhold

/**
 * Nisifret nummer som entydig identifiserer enheter i Enhetsregisteret.
 */
data class Organisasjonsnummer(private val organisasjonsnummer: String) {
    fun asString(): String = organisasjonsnummer
}