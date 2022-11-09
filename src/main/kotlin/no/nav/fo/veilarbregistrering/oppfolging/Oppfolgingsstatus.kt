package no.nav.fo.veilarbregistrering.oppfolging

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe

data class Oppfolgingsstatus(
    val isUnderOppfolging: Boolean,
    val kanReaktiveres: Boolean? = null,
    val erSykmeldtMedArbeidsgiver: Boolean? = null,
    val formidlingsgruppe: Formidlingsgruppe? = null,
    val servicegruppe: Servicegruppe? = null,
    val rettighetsgruppe: Rettighetsgruppe? = null,
)
