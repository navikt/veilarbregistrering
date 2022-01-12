package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import java.util.*

class BrukersTilstand private constructor(
    val oppfolgingStatusData: Oppfolgingsstatus,
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

    val isErSykmeldtMedArbeidsgiver: Boolean
        get() = oppfolgingStatusData.erSykmeldtMedArbeidsgiver == true
    val isUnderOppfolging: Boolean
        get() = oppfolgingStatusData.isUnderOppfolging
    val formidlingsgruppe: Optional<Formidlingsgruppe>
        get() = Optional.ofNullable(oppfolgingStatusData.formidlingsgruppe)
    val servicegruppe: Optional<Servicegruppe>
        get() = Optional.ofNullable(oppfolgingStatusData.servicegruppe)
    val rettighetsgruppe: Optional<Rettighetsgruppe>
        get() = Optional.ofNullable(oppfolgingStatusData.rettighetsgruppe)

    companion object Factory {
        fun create(oppfolgingsstatus: Oppfolgingsstatus, harIgangsattGjenopptagbarRegistrering: Boolean) : BrukersTilstand {
            val registreringstype = beregnRegistreringType(oppfolgingsstatus)
            return BrukersTilstand(
                oppfolgingsstatus,
                registreringstype,
                registreringstype === RegistreringType.ORDINAER_REGISTRERING && harIgangsattGjenopptagbarRegistrering)
        }

        private fun beregnRegistreringType(oppfolgingsstatus: Oppfolgingsstatus): RegistreringType {
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
    }
}