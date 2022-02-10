package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import java.util.*

class BrukersTilstand private constructor(
    oppfolgingStatusData: Oppfolgingsstatus,
    val registreringstype: RegistreringType,
    val isHarIgangsattGjenopptagbarRegistrering: Boolean) {

    fun kanReaktiveres(): Boolean {
        return RegistreringType.REAKTIVERING == registreringstype
    }

    fun ikkeErOrdinaerRegistrering(): Boolean {
        return RegistreringType.ORDINAER_REGISTRERING != registreringstype
    }

    fun ikkeErSykemeldtRegistrering(): Boolean {
        return RegistreringType.SYKMELDT_REGISTRERING != registreringstype
    }

    val isErSykmeldtMedArbeidsgiver: Boolean = oppfolgingStatusData.erSykmeldtMedArbeidsgiver == true
    val isUnderOppfolging: Boolean = oppfolgingStatusData.isUnderOppfolging
    val formidlingsgruppe: Formidlingsgruppe? = oppfolgingStatusData.formidlingsgruppe
    val servicegruppe: Servicegruppe? = oppfolgingStatusData.servicegruppe
    val rettighetsgruppe: Rettighetsgruppe? = oppfolgingStatusData.rettighetsgruppe

    companion object Factory {
        fun create(oppfolgingsstatus: Oppfolgingsstatus, harIgangsattGjenopptagbarRegistrering: Boolean) : BrukersTilstand {
            val registreringstype = beregnRegistreringType(oppfolgingsstatus)
            return BrukersTilstand(
                oppfolgingsstatus,
                registreringstype,
                registreringstype === RegistreringType.ORDINAER_REGISTRERING && harIgangsattGjenopptagbarRegistrering)
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