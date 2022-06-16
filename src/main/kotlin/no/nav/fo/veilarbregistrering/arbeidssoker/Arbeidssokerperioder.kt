package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Periode
import java.util.Objects

class Arbeidssokerperioder(arbeidssokerperioder: List<Formidlingsgruppeperiode>?) {

    private val arbeidssokerperioder: List<Formidlingsgruppeperiode> = arbeidssokerperioder ?: emptyList()

    fun overlapperMed(forespurtPeriode: Periode): Arbeidssokerperioder {
        return Arbeidssokerperioder(arbeidssokerperioder
            .filter { it.periode.overlapperMed(forespurtPeriode) && it.formidlingsgruppe.erArbeidssoker() }
        )
    }

    fun dekkerHele(forespurtPeriode: Periode): Boolean {
        val eldsteFormidlingsgruppeperiode: Formidlingsgruppeperiode? = arbeidssokerperioder.minByOrNull { it.periode.fra }

        return eldsteFormidlingsgruppeperiode?.let { forespurtPeriode.fraOgMed(it.periode) } ?: false
    }

    fun asList(): List<Formidlingsgruppeperiode> {
        return arbeidssokerperioder
    }

    fun eldsteFoerst(): List<Formidlingsgruppeperiode> {
        return arbeidssokerperioder.sortedBy{ it.periode.fra }
    }

    override fun toString(): String {
        return "{arbeidssokerperioder=$arbeidssokerperioder}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Arbeidssokerperioder
        return arbeidssokerperioder == that.arbeidssokerperioder
    }

    override fun hashCode(): Int {
        return Objects.hash(arbeidssokerperioder)
    }

    companion object {
        fun of(arbeidssokerperioder: List<Formidlingsgruppeperiode>?): Arbeidssokerperioder {
            return Arbeidssokerperioder(arbeidssokerperioder)
        }
    }

}