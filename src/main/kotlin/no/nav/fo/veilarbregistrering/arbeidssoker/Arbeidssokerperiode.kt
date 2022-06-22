package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Periode

data class Arbeidssokerperiode(val periode: Periode) {

    fun erGjeldende() : Boolean {
        return periode.erApen()
    }

    override fun toString() = "{fraOgMed=${periode.fra}, tilOgMed=${periode.til}}"

    companion object {
        fun of(periode: Periode): Arbeidssokerperiode {
            return Arbeidssokerperiode(periode)
        }
    }
}

