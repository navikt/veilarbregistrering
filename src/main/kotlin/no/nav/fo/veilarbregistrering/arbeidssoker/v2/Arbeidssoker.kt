package no.nav.fo.veilarbregistrering.arbeidssoker.v2

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.Reaktivering
import java.time.LocalDate
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
        this.tilstand = TidligereArbeidssokerState
        //publish event
        observers.forEach { it.update("ArbeidssokerperiodeAvsluttetEvent") }
    }

    internal fun harVærtInaktivMerEnn28Dager() = sistePeriode()!!.tilDato!!.isBefore(LocalDateTime.now().minusDays(28))

    internal fun ikkeHarTidligerePerioder(): Boolean = arbeidssokerperioder.isEmpty()

    internal fun sistePeriode(): Arbeidssokerperiode? {
        if (arbeidssokerperioder.isEmpty()) return null
        return arbeidssokerperioder.sortedBy { it.fraDato }.last()
    }

    internal fun allePerioder(): List<Arbeidssokerperiode> {
        return arbeidssokerperioder
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
 * Ikke arbeidssøker betyr at du aldri har vært arbeidssøker.
 */
private object IkkeArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering) {
        logger.info("Starter arbeidssøkerperiode som følge av ordinær registrering")
        arbeidssoker.startPeriode(ordinaerBrukerRegistrering.opprettetDato)
    }

    override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: Reaktivering) {
        logger.warn("Arbeidssøker har ingen tidligere arbeidssøkerperioder - kan derfor ikke reaktiveres")
        return
    }

    override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {
        if (!formidlingsgruppeEndretEvent.formidlingsgruppe.erArbeidssoker()) {
            logger.warn("Forkaster formidlingsgruppeEndretEvent med " +
                    "${formidlingsgruppeEndretEvent.formidlingsgruppe} da Arbeidssøker ikke har noe historikk")
            return
        }
        logger.info("Arbeidssøkerperioden ble initiert av en formidlingsgruppe - ikke en ordinær/reaktivert registrering")
        arbeidssoker.startPeriode(formidlingsgruppeEndretEvent.formidlingsgruppeEndret)
    }

    override fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEvent) {
        if (!meldekortEvent.erArbeidssokerNestePeriode) {
            logger.info("Arbeidssøker er allerede inaktiv")
            return
        }
        logger.warn("Arbeidssøkerperioden ble initiert av et meldekort - ikke en ordinær/reaktivert registrering")
        arbeidssoker.startPeriode(meldekortEvent.eventOpprettet)
    }
}

/**
* Aktiv arbeidssøker betyr at bruker har en åpen periode - at perioden ikke er avsluttet og at tildato er null.
 */
private object AktivArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering) {
        logger.warn("Avviser OrdinaerBrukerRegistrering - Arbeidssøker er allerede aktiv")
    }

    override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: Reaktivering) {
        logger.warn("Avviser Reaktivering - Arbeidssøker er allerede aktiv")
    }

    override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {
        if (formidlingsgruppeEndretEvent.formidlingsgruppe.erArbeidssoker()) {
            logger.info("Avviser FormidlingsgruppeEndretEvent - Arbeidssøker er allerede aktiv")
            return
        }
        logger.info("Avslutter arbeiddssøkerperiode som følge av ${formidlingsgruppeEndretEvent.formidlingsgruppe}")
        arbeidssoker.avsluttPeriode(formidlingsgruppeEndretEvent.formidlingsgruppeEndret)
    }

    override fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEvent) {
        if (meldekortEvent.erArbeidssokerNestePeriode) {
            logger.info("Arbeidssøker ønsker å stå som aktiv også neste periode")
            return
        }
        logger.info("Avslutter arbeidssøkerperiode som følge av NEI i meldekortet")
        arbeidssoker.avsluttPeriode(meldekortEvent.eventOpprettet)
    }
}

/**
 * Tidligere arbeidssøker betyr at du tidligere har vært arbeidssøker, men ikke er det lenger.
*/
private object TidligereArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering) {
        logger.info("Starter arbeidssøkerperiode som følge av ordinær registrering")

        if (kanSistePeriodeGjenåpnes(arbeidssoker, ordinaerBrukerRegistrering.opprettetDato.toLocalDate())) {
            logger.info("Reåpner tidligere arbeidssøkerperiode som følge av ordinær registrering")
            arbeidssoker.sistePeriode()!!.gjenåpne()
        } else {
            arbeidssoker.startPeriode(ordinaerBrukerRegistrering.opprettetDato)
        }
    }

    override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: Reaktivering) {
        if (arbeidssoker.ikkeHarTidligerePerioder())
            throw IllegalStateException("Tilstanden er feil - TidligereArbeidssokerState skal alltid ha tidligere perioder.")

        if (arbeidssoker.harVærtInaktivMerEnn28Dager()) {
            logger.warn("Arbeidssøker har vært inaktiv mer enn 28 dager - kan derfor ikke reaktiveres")
            return
        }
        logger.info("Starter arbeidssøkerperiode som følge av reaktivering")
        arbeidssoker.startPeriode(reaktivering.opprettetTidspunkt)
    }

    override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {
        if (!formidlingsgruppeEndretEvent.formidlingsgruppe.erArbeidssoker()) {
            logger.info("Avviser FormidlingsgruppeEndretEvent ${formidlingsgruppeEndretEvent.formidlingsgruppe} - Arbeidssøker er allerede inaktiv")
            return
        }

        if (kanSistePeriodeGjenåpnes(arbeidssoker, formidlingsgruppeEndretEvent.formidlingsgruppeEndret.toLocalDate())) {
            logger.warn("Reåpner tidligere arbeidssøkerperiode som følge av formidlingsgruppe")
            arbeidssoker.sistePeriode()!!.gjenåpne()
        } else {
            logger.warn("Arbeidssøkerperioden ble initiert av en formidlingsgruppe - ikke en ordinær/reaktivert registrering")
            arbeidssoker.startPeriode(formidlingsgruppeEndretEvent.formidlingsgruppeEndret)
        }
    }

    private fun kanSistePeriodeGjenåpnes(arbeidssoker: Arbeidssoker, registreringstidspunkt: LocalDate) =
        arbeidssoker.sistePeriode()!!.tilDato?.toLocalDate() == registreringstidspunkt

    override fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEvent) {
        if (!meldekortEvent.erArbeidssokerNestePeriode) {
            logger.info("Avviser MeldekortEventArbeidssøker er allerede inaktiv")
            return
        }
        logger.warn("Arbeidssøkerperioden ble initiert av et meldekort - ikke en ordinær/reaktivert registrering")
        arbeidssoker.startPeriode(meldekortEvent.eventOpprettet)
    }

}

