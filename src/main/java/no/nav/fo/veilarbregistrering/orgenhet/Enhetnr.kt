package no.nav.fo.veilarbregistrering.orgenhet

/**
 * Alle enheter har en 4-sifret kode i Norg.
 */
data class Enhetnr private constructor(private val enhetNr: String) {
    fun asString(): String {
        return enhetNr
    }

    companion object {
        fun of(enhetId: String): Enhetnr = Enhetnr(enhetId)
        fun internBrukerstotte(): Enhetnr = of("2930")

        /**
         * NAV Vikafossen håndterer brukere med adressebeskyttelse
         */
        fun enhetForAdressebeskyttelse(): Enhetnr = of("2103")
    }
}