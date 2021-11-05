package no.nav.fo.veilarbregistrering.bruker

import java.time.LocalDate
import java.time.Period

data class Foedselsdato (val foedselsdato: LocalDate) {
    fun alderPaa(dato: LocalDate): Int {
        return Period.between(foedselsdato, dato).years
    }
}