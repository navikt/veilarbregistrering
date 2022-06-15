package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Periode
import java.time.LocalDate

data class Formidlingsgruppeperiode(val formidlingsgruppe: Formidlingsgruppe, val periode: Periode) {
    fun tilOgMed(tilDato: LocalDate?): Formidlingsgruppeperiode {
        return of(
            formidlingsgruppe,
            periode.tilOgMed(tilDato)
        )
    }

    override fun toString() = "{formidlingsgruppe=$formidlingsgruppe, fraOgMed=${periode.fra}, tilOgMed=${periode.til}}"

    companion object {
        fun of(formidlingsgruppe: Formidlingsgruppe, periode: Periode): Formidlingsgruppeperiode {
            return Formidlingsgruppeperiode(formidlingsgruppe, periode)
        }
    }
}

