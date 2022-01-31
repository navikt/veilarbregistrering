package no.nav.fo.veilarbregistrering.bruker


import java.time.LocalDate

data class Foedselsnummer(val foedselsnummer: String) {

    fun stringValue(): String {
        return foedselsnummer
    }

    fun maskert(): String {
        return foedselsnummer.replace("[0-9]{11}".toRegex(), "***********")
    }

    fun alder(dato: LocalDate?): Int {
        return FnrUtils.utledAlderForFnr(foedselsnummer, dato)
    }

    companion object {
        @JvmStatic
        fun of(foedselsnummer: String): Foedselsnummer {
            return Foedselsnummer(foedselsnummer)
        }
    }
}