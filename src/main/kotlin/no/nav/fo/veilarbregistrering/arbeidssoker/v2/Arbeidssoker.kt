package no.nav.fo.veilarbregistrering.arbeidssoker.v2

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.Reaktivering
import java.time.LocalDateTime

/**
 * Root aggregate - all kommunikasjon mot Arbeidssøker og underliggende elementer skal gå via dette objektet.
 */
class Arbeidssoker : Observable {

    private val observers: MutableList<Observer> = mutableListOf()
    private var tilstand: ArbeidssokerState = IkkeArbeidssokerState
    private var arbeidssokerperioder: MutableList<Arbeidssokerperiode> = mutableListOf()

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

    internal fun startPeriode(fraDato: LocalDateTime) {
        this.arbeidssokerperioder.add(Arbeidssokerperiode(fraDato, null))
        this.tilstand = AktivArbeidssokerState
        //publish event
        observers.forEach { it.update("ArbeidssokerperiodeStartetEvent") }
    }

    internal fun avsluttPeriode(tilDato: LocalDateTime) {
        sistePeriode()?.avslutt(tilDato) ?: throw IllegalStateException("Kan ikke avslutte en periode som ikke finnes")
        this.tilstand = IkkeArbeidssokerState
        //publish event
        observers.forEach { it.update("ArbeidssokerperiodeAvsluttetEvent") }
    }

    fun accept(arbeidssokerVisitor: ArbeidssokerVisitor) {
        arbeidssokerVisitor.visitSistePeriode(sistePeriode())
        arbeidssokerVisitor.visitPerioder(arbeidssokerperioder)
    }

    internal fun harVærtInaktivMerEnn28Dager() = sistePeriode()!!.tilDato!!.isBefore(LocalDateTime.now().minusDays(28))

    internal fun ikkeHarTidligerePerioder(): Boolean = arbeidssokerperioder.isEmpty()

    private fun sistePeriode(): Arbeidssokerperiode? {
        if (arbeidssokerperioder.isEmpty()) return null
        return arbeidssokerperioder.sortedBy { it.fraDato }.last()
    }

    override fun add(observer: Observer) {
        observers.add(observer)
    }

    override fun remove(observer: Observer) {
        observers.remove(observer)
    }
}

private interface ArbeidssokerState {
    fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering)
    fun behandle(arbeidssoker: Arbeidssoker, reaktivering: Reaktivering)
    fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent)
    fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEvent)
}

/**
 * Ikke arbeidssøker betyr at du ikke er aktiv arbeidssøker.
 * Det kan bety 1 av 2: Du har aldri vært arbeidssøker eller du har tidligere vært det, men er det ikke lenger.
 */
private object IkkeArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering) {
        logger.info("Starter arbeidssøkerperiode som følge av ordinær registrering")
        arbeidssoker.startPeriode(ordinaerBrukerRegistrering.opprettetDato)
    }

    override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: Reaktivering) {
        if (arbeidssoker.ikkeHarTidligerePerioder()) {
            logger.warn("Arbeidssøker har ingen tidligere arbeidssøkerperioder - kan derfor ikke reaktiveres")
            return
        }
        if (arbeidssoker.harVærtInaktivMerEnn28Dager()) {
            logger.warn("Arbeidssøker har vært inaktiv mer enn 28 dager - kan derfor ikke reaktiveres")
            return
        }
        logger.info("Starter arbeidssøkerperiode som følge av reaktivering")
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

/**
 * Aktiv arbeidssøker betyr at bruker har en åpen periode - at perioden ikke er avsluttet og at tildato er null.
 */
private object AktivArbeidssokerState : ArbeidssokerState {
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

