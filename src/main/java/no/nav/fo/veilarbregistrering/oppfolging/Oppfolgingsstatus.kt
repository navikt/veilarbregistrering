package no.nav.fo.veilarbregistrering.oppfolging

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe

data class Oppfolgingsstatus(
    val isUnderOppfolging: Boolean,
    val kanReaktiveres: Boolean?,
    val erSykmeldtMedArbeidsgiver: Boolean?,
    val formidlingsgruppe: Formidlingsgruppe?,
    val servicegruppe: Servicegruppe?,
    val rettighetsgruppe: Rettighetsgruppe?)
