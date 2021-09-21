package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Periode
import java.util.*
import java.util.stream.Collectors

class Arbeidssokerperioder(arbeidssokerperioder: List<Arbeidssokerperiode>?) {
    private val arbeidssokerperioder: List<Arbeidssokerperiode> = arbeidssokerperioder ?: emptyList()
    fun overlapperMed(forespurtPeriode: Periode?): Arbeidssokerperioder {
        return Arbeidssokerperioder(arbeidssokerperioder.stream()
            .filter { p: Arbeidssokerperiode -> p.periode.overlapperMed(forespurtPeriode) }
            .filter { p: Arbeidssokerperiode -> p.formidlingsgruppe.erArbeidssoker() }
            .collect(Collectors.toList()))
    }

    fun dekkerHele(forespurtPeriode: Periode): Boolean {
        val eldsteArbeidssokerperiode = arbeidssokerperioder.stream()
            .min(Comparator.comparing { e: Arbeidssokerperiode -> e.periode.fra })
        return eldsteArbeidssokerperiode
            .map { arbeidssokerperiode: Arbeidssokerperiode -> forespurtPeriode.fraOgMed(arbeidssokerperiode.periode) }
            .orElse(false)
    }

    fun asList(): List<Arbeidssokerperiode> {
        return arbeidssokerperioder
    }

    fun eldsteFoerst(): List<Arbeidssokerperiode> {
        return arbeidssokerperioder.stream()
            .sorted(Comparator.comparing { e: Arbeidssokerperiode -> e.periode.fra })
            .collect(Collectors.toList())
    }

    override fun toString(): String {
        return "{" +
                "arbeidssokerperioder=" + arbeidssokerperioder +
                '}'
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