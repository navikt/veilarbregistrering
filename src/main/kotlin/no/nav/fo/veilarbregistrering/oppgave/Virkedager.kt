package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.oppgave.Ukedag.erHelg
import no.nav.fo.veilarbregistrering.oppgave.Helligdager.erHelligdag
import no.nav.fo.veilarbregistrering.oppgave.Ukedag
import no.nav.fo.veilarbregistrering.oppgave.Helligdager
import java.time.LocalDate

internal object Virkedager {
    fun plussAntallArbeidsdager(dato: LocalDate, antallDager: Int): LocalDate {
        var teller = 0
        var tellerDager = 0
        while (teller < antallDager) {
            tellerDager++
            if (erHelg(dato.plusDays(tellerDager.toLong()))) {
                continue
            }
            if (erHelligdag(dato.plusDays(tellerDager.toLong()))) {
                continue
            }
            teller++
        }
        return dato.plusDays(tellerDager.toLong())
    }
}