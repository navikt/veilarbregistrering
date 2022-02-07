package no.nav.fo.veilarbregistrering.oppgave

import java.time.DayOfWeek
import no.nav.fo.veilarbregistrering.oppgave.Ukedag
import java.time.LocalDate
import java.util.*

internal object Ukedag {
    private val HELG = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    @JvmStatic
    fun erHelg(dato: LocalDate): Boolean =
        dato.dayOfWeek in HELG
}