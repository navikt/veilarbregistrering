package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Periode
import java.time.LocalDate

data class Arbeidssokerperiode(val formidlingsgruppe: Formidlingsgruppe, val periode: Periode) {
    fun tilOgMed(tilDato: LocalDate?): Arbeidssokerperiode {
        return of(
            formidlingsgruppe,
            periode.tilOgMed(tilDato)
        )
    }

    fun erGjeldende() : Boolean {
        return periode.erApen()
    }

    override fun toString() = "{formidlingsgruppe=$formidlingsgruppe, fraOgMed=${periode.fra}, tilOgMed=${periode.til}}"

    companion object {
        fun of(formidlingsgruppe: Formidlingsgruppe, periode: Periode): Arbeidssokerperiode {
            return Arbeidssokerperiode(formidlingsgruppe, periode)
        }
    }
}

