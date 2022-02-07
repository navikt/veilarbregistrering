package no.nav.fo.veilarbregistrering.orgenhet

/**
 * Alle enheter har en 4-sifret kode i Norg.
 */
data class Enhetnr(private val enhetNr: String) {
    fun asString(): String {
        return enhetNr
    }

    companion object {
        fun internBrukerstotte() = Enhetnr("2930")

        /**
         * NAV Vikafossen håndterer brukere med adressebeskyttelse
         */
        fun enhetForAdressebeskyttelse() = Enhetnr("2103")
    }
}