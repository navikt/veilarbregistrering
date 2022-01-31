package no.nav.fo.veilarbregistrering.bruker


import no.bekk.bekkopen.person.FodselsnummerValidator
import java.time.LocalDate
import java.time.Period

data class Foedselsnummer(val foedselsnummer: String) {

    fun stringValue(): String = foedselsnummer
    fun maskert(): String = foedselsnummer.replace("[0-9]{11}".toRegex(), "***********")
    fun alder(dato: LocalDate): Int = FnrUtils.utledAlderForFnr(foedselsnummer, dato)
}

internal object FnrUtils {
    fun utledAlderForFnr(fnr: String, dagensDato: LocalDate): Int {
        return antallAarSidenDato(utledFodselsdatoForFnr(fnr), dagensDato)
    }

    fun utledFodselsdatoForFnr(fnr: String): LocalDate {
        val fodselsnummer = FodselsnummerValidator.getFodselsnummer(fnr)
        return LocalDate.of(
            fodselsnummer.birthYear.toInt(),
            fodselsnummer.month.toInt(),
            fodselsnummer.dayInMonth.toInt()
        )
    }

    fun antallAarSidenDato(dato: LocalDate?, dagensDato: LocalDate): Int {
        return Period.between(dato, dagensDato).years
    }
}