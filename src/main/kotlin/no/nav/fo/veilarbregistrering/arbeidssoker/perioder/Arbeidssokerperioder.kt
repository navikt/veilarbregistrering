package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.v2.ArbeidssokerVisitor
import no.nav.fo.veilarbregistrering.bruker.Periode
import java.util.Objects

class Arbeidssokerperioder(arbeidssokerperioder: List<Arbeidssokerperiode>?) {

    private val arbeidssokerperioder: List<Arbeidssokerperiode> = arbeidssokerperioder ?: emptyList()

    fun overlapperMed(forespurtPeriode: Periode): Arbeidssokerperioder {
        return Arbeidssokerperioder(arbeidssokerperioder
            .filter { it.periode.overlapperMed(forespurtPeriode) }
        )
    }

    fun dekkerHele(forespurtPeriode: Periode): Boolean {
        val eldsteArbeidssokerperiode: Arbeidssokerperiode? = arbeidssokerperioder.minByOrNull { it.periode.fra }

        return eldsteArbeidssokerperiode?.let { forespurtPeriode.fraOgMed(it.periode) } ?: false
    }

    fun asList(): List<Arbeidssokerperiode> {
        return arbeidssokerperioder
    }

    fun nyestePeriode(): Arbeidssokerperiode? {
        return this.eldsteFoerst().lastOrNull()
    }

    fun eldsteFoerst(): List<Arbeidssokerperiode> {
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
        fun of(arbeidssokerperioder: List<Arbeidssokerperiode>?): Arbeidssokerperioder {
            return Arbeidssokerperioder(arbeidssokerperioder)
        }
    }

}