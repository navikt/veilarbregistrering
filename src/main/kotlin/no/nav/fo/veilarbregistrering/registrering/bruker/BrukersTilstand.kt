package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe

class BrukersTilstand private constructor(
    private val oppfolgingStatusData: Oppfolgingsstatus,
    val registreringstype: RegistreringType
) {

    fun kanReaktiveres(): Boolean {
        return RegistreringType.REAKTIVERING == registreringstype
    }

    fun ikkeErOrdinaerRegistrering(): Boolean {
        return RegistreringType.ORDINAER_REGISTRERING != registreringstype
    }

    fun ikkeErSykemeldtRegistrering(): Boolean {
        return RegistreringType.SYKMELDT_REGISTRERING != registreringstype
    }

    override fun toString(): String {
        return "BrukersTilstand(" +
                "oppfolgingsstatus=$oppfolgingStatusData, " +
                "registreringstype=$registreringstype)"
    }

    val isErSykmeldtMedArbeidsgiver: Boolean = oppfolgingStatusData.erSykmeldtMedArbeidsgiver == true
    val isUnderOppfolging: Boolean = oppfolgingStatusData.isUnderOppfolging
    val formidlingsgruppe: Formidlingsgruppe? = oppfolgingStatusData.formidlingsgruppe
    val servicegruppe: Servicegruppe? = oppfolgingStatusData.servicegruppe
    val rettighetsgruppe: Rettighetsgruppe? = oppfolgingStatusData.rettighetsgruppe

    companion object Factory {
        fun create(oppfolgingsstatus: Oppfolgingsstatus) : BrukersTilstand {
            return BrukersTilstand(oppfolgingsstatus, beregnRegistreringType(oppfolgingsstatus))
        }
    }
}

fun beregnRegistreringType(oppfolgingsstatus: Oppfolgingsstatus): RegistreringType {
    return if (oppfolgingsstatus.isUnderOppfolging && oppfolgingsstatus.kanReaktiveres != true) { // underoppfolging OG ikke kanreaktiveres
        RegistreringType.ALLEREDE_REGISTRERT
    } else if (oppfolgingsstatus.kanReaktiveres == true) {
        RegistreringType.REAKTIVERING
    } else if (oppfolgingsstatus.erSykmeldtMedArbeidsgiver == true) {
        RegistreringType.SYKMELDT_REGISTRERING
    } else {
        RegistreringType.ORDINAER_REGISTRERING
    }
}