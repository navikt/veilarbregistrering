package no.nav.fo.veilarbregistrering.oppfolging

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe

data class Oppfolgingsstatus(
    val isUnderOppfolging: Boolean,
    val kanReaktiveres: Boolean? = null,
    val erSykmeldtMedArbeidsgiver: Boolean? = null,
    val formidlingsgruppe: Formidlingsgruppe? = null,
    val servicegruppe: Servicegruppe? = null,
    val rettighetsgruppe: Rettighetsgruppe? = null,
) {
    fun erLikBortsettFraKanReaktiveres(annen: Oppfolgingsstatus): Boolean {
        return isUnderOppfolging == annen.isUnderOppfolging && erSykmeldtMedArbeidsgiver == annen.erSykmeldtMedArbeidsgiver
                && formidlingsgruppe == annen.formidlingsgruppe && servicegruppe == annen.servicegruppe && rettighetsgruppe == annen.rettighetsgruppe
    }
}
