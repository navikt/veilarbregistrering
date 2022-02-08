package no.nav.fo.veilarbregistrering.oppgave

import java.time.DayOfWeek
import java.time.LocalDate

internal object Ukedag {
    private val HELG = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    fun erHelg(dato: LocalDate): Boolean = dato.dayOfWeek in HELG
}