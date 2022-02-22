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
    fun manglerArenstatusFraNyKilde(nyKilde: Oppfolgingsstatus): Boolean =
        nyKilde.formidlingsgruppe == null && nyKilde.rettighetsgruppe == null && nyKilde.servicegruppe == null && formidlingsgruppe != null && servicegruppe != null && rettighetsgruppe != null
}
