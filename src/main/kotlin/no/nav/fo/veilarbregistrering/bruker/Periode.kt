package no.nav.fo.veilarbregistrering.bruker

import java.time.LocalDate

data class Periode (val fra: LocalDate, val til: LocalDate?) : Comparable<Periode> {

    init {
        require(!(til != null && fra.isAfter(til))) { "FraOgMed-dato er etter TilOgMed-dato" }
    }

    fun tilOgMed(tilDato: LocalDate?): Periode {
        return Periode(fra, tilDato)
    }

    /**
     * Er periode er Åpen, dersom "til"-dato er null.
     */
    fun erApen(): Boolean = til == null

    fun fraDatoSomUtcString(): String {
        return fra.toString()
    }

    fun tilDatoSomUtcString(): String? {
        return til?.toString()
    }

    fun overlapperMed(forespurtPeriode: Periode): Boolean {
        if (forespurtPeriodeAvsluttesFørPeriodeStarter(forespurtPeriode)) {
            return false
        }
        return !forespurtPeriodeStarterEtterPeriodeErAvsluttet(forespurtPeriode)
    }

    private fun forespurtPeriodeStarterEtterPeriodeErAvsluttet(forespurtPeriode: Periode): Boolean {
        return til != null && forespurtPeriode.fra.isAfter(til)
    }

    private fun forespurtPeriodeAvsluttesFørPeriodeStarter(forespurtPeriode: Periode): Boolean {
        return forespurtPeriode.til != null && fra.isAfter(forespurtPeriode.til)
    }

    fun fraOgMed(periode: Periode): Boolean {
        return fra == periode.fra || fra.isAfter(periode.fra)
    }

    override fun compareTo(other: Periode): Int {
        return fra.compareTo(other.fra)
    }

    companion object {
        fun gyldigPeriode(fraOgMed: LocalDate?, tilOgMed: LocalDate?): Periode = Periode(requireNotNull(fraOgMed), tilOgMed)
    }
}