package no.nav.fo.veilarbregistrering.arbeidssoker.v2

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.Reaktivering
import java.time.LocalDateTime

class Arbeidssoker {

    private var tilstand: ArbeidssokerState = IkkeArbeidssokerState

    private var fraDato: LocalDateTime? = null
    private var tilDato: LocalDateTime? = null

    fun behandle(ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering) {
        tilstand.behandle(this, ordinaerBrukerRegistrering)
    }

    fun behandle(reaktivering: Reaktivering) {
        tilstand.behandle(this, reaktivering)
    }

    fun behandle(formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {
        tilstand.behandle(this, formidlingsgruppeEndretEvent)
    }

    fun behandle(meldekortEvent: MeldekortEvent) {
        tilstand.behandle(this, meldekortEvent)
    }

    internal fun avsluttPeriode(tilDato: LocalDateTime) {
        this.tilDato = tilDato
        this.tilstand = IkkeArbeidssokerState
        //publish event
    }

    internal fun startPeriode(fraDato: LocalDateTime) {
        this.fraDato = fraDato
        this.tilstand = AktivArbeidssokerState
        //publish event
    }
}

object IkkeArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering) {
        arbeidssoker.startPeriode(ordinaerBrukerRegistrering.opprettetDato)
    }

    override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: Reaktivering) {
        arbeidssoker.startPeriode(reaktivering.opprettetTidspunkt)
    }

    override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {
        if (formidlingsgruppeEndretEvent.formidlingsgruppe.erArbeidssoker()) {
            logger.warn("Arbeidssøkerperioden ble initiert av en formidlingsgruppe - ikke en ordinær/reaktivert registrering")
            arbeidssoker.startPeriode(formidlingsgruppeEndretEvent.formidlingsgruppeEndret)
        } else {
            logger.info("Arbeidssøker er allerede inaktiv")
        }
    }

    override fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEvent) {
        if (meldekortEvent.erArbeidssokerNestePeriode) {
            logger.warn("Arbeidssøkerperioden ble initiert av et meldekort - ikke en ordinær/reaktivert registrering")
            arbeidssoker.startPeriode(meldekortEvent.eventOpprettet)
        } else {
            logger.info("Arbeidssøker er allerede inaktiv")
        }
    }
}

object AktivArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering) {
        logger.warn("Arbeidssøker er allerede aktiv")
    }

    override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: Reaktivering) {
        logger.warn("Arbeidssøker er allerede aktiv")
    }

    override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {
        if (formidlingsgruppeEndretEvent.formidlingsgruppe.erArbeidssoker()) {
            logger.warn("Arbeidssøker er allerede aktiv")
        } else {
            logger.info("Avslutter arbeiddssøkerperiode som følge av ${formidlingsgruppeEndretEvent.formidlingsgruppe}")
            arbeidssoker.avsluttPeriode(formidlingsgruppeEndretEvent.formidlingsgruppeEndret)
        }
    }

    override fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEvent) {
        if (meldekortEvent.erArbeidssokerNestePeriode) {
            logger.info("Arbeidssøker ønsker å stå som aktiv også neste periode")
        } else {
            logger.info("Avslutter arbeidssøkerperiode som følge av NEI i meldekortet")
            arbeidssoker.avsluttPeriode(meldekortEvent.eventOpprettet)
        }
    }

}