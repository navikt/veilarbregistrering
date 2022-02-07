package no.nav.fo.veilarbregistrering.oppfolging.adapter

data class OppfolgingStatusData(
    val underOppfolging: Boolean = false,
    val kanReaktiveres: Boolean? = null,
    val erSykmeldtMedArbeidsgiver: Boolean? = null,
    val formidlingsgruppe: String? = null,
    val servicegruppe: String? = null,
    val rettighetsgruppe: String? = null,
) {

    fun withUnderOppfolging(underOppfolging: Boolean): OppfolgingStatusData {
        return if (this.underOppfolging == underOppfolging) this else OppfolgingStatusData(
            underOppfolging,
            kanReaktiveres,
            erSykmeldtMedArbeidsgiver,
            formidlingsgruppe,
            servicegruppe,
            rettighetsgruppe,
        )
    }

    fun withKanReaktiveres(kanReaktiveres: Boolean): OppfolgingStatusData {
        return if (this.kanReaktiveres == kanReaktiveres) this else OppfolgingStatusData(
            underOppfolging,
            kanReaktiveres,
            erSykmeldtMedArbeidsgiver,
            formidlingsgruppe,
            servicegruppe,
            rettighetsgruppe,
        )
    }

    fun withErSykmeldtMedArbeidsgiver(erSykmeldtMedArbeidsgiver: Boolean): OppfolgingStatusData {
        return if (this.erSykmeldtMedArbeidsgiver == erSykmeldtMedArbeidsgiver) this else OppfolgingStatusData(
            underOppfolging,
            kanReaktiveres,
            erSykmeldtMedArbeidsgiver,
            formidlingsgruppe,
            servicegruppe,
            rettighetsgruppe,
        )
    }
}