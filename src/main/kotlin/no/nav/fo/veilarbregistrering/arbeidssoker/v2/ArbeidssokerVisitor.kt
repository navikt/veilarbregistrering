package no.nav.fo.veilarbregistrering.arbeidssoker.v2

/**
 * Arbeidssoker støtter ulike "besøkende"
 */
interface ArbeidssokerVisitor {

    fun visitSistePeriode(lastOrNull: Arbeidssokerperiode?) {}
    fun visitPerioder(arbeidssokerperioder: MutableList<Arbeidssokerperiode>) {}
}
