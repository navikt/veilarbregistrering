package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.log.logger
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Root aggregate - all kommunikasjon mot Arbeidssøker og underliggende elementer skal gå via dette objektet.
 */
class Arbeidssoker : Observable {

    private val observers: MutableList<Observer> = mutableListOf()
    private var tilstand: ArbeidssokerState = IkkeArbeidssokerState
    private var arbeidssokerperioder: MutableList<Arbeidssokerperiode> = mutableListOf()

    fun behandle(endreArbeidssøker: EndreArbeidssøker) {
        when (endreArbeidssøker) {
            is RegistrerArbeidssøker -> tilstand.behandle(this, endreArbeidssøker)
            is ReaktiverArbeidssøker -> tilstand.behandle(this, endreArbeidssøker)
            is FormidlingsgruppeEndret -> tilstand.behandle(this, endreArbeidssøker)
            is MeldekortEndret -> tilstand.behandle(this, endreArbeidssøker)
        }
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
    fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker)
    fun behandle(arbeidssoker: Arbeidssoker, reaktivering: ReaktiverArbeidssøker)
    fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndret)
    fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEndret)
}

/**
 * Ikke arbeidssøker betyr at du aldri har vært arbeidssøker.
 */
private object IkkeArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker) {
        logger.info("Starter arbeidssøkerperiode som følge av ordinær registrering")
        arbeidssoker.startPeriode(ordinaerBrukerRegistrering.opprettetTidspunkt())
    }

    override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: ReaktiverArbeidssøker) {
        logger.warn("Arbeidssøker har ingen tidligere arbeidssøkerperioder - kan derfor ikke reaktiveres")
        return
    }

    override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndret) {
        if (!formidlingsgruppeEndretEvent.formidlingsgruppe().erArbeidssoker()) {
            logger.warn("Forkaster formidlingsgruppeEndretEvent med " +
                    "${formidlingsgruppeEndretEvent.formidlingsgruppe()} da Arbeidssøker ikke har noe historikk")
            return
        }
        logger.info("Arbeidssøkerperioden ble initiert av en formidlingsgruppe - ikke en ordinær/reaktivert registrering")
        arbeidssoker.startPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())
    }

    override fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEndret) {
        if (!meldekortEvent.erArbeidssokerNestePeriode()) {
            logger.info("Arbeidssøker er allerede inaktiv")
            return
        }
        logger.warn("Arbeidssøkerperioden ble initiert av et meldekort - ikke en ordinær/reaktivert registrering")
        arbeidssoker.startPeriode(meldekortEvent.opprettetTidspunkt())
    }
}

/**
* Aktiv arbeidssøker betyr at bruker har en åpen periode - at perioden ikke er avsluttet og at tildato er null.
 */
private object AktivArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker) {
        logger.warn("Avviser OrdinaerBrukerRegistrering - Arbeidssøker er allerede aktiv")
    }

    override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: ReaktiverArbeidssøker) {
        logger.warn("Avviser Reaktivering - Arbeidssøker er allerede aktiv")
    }

    override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndret) {
        if (formidlingsgruppeEndretEvent.formidlingsgruppe().erArbeidssoker()) {
            logger.info("Avviser FormidlingsgruppeEndretEvent - Arbeidssøker er allerede aktiv")
            return
        }
        logger.info("Avslutter arbeiddssøkerperiode som følge av ${formidlingsgruppeEndretEvent.formidlingsgruppe()}")
        arbeidssoker.avsluttPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())
    }

    override fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEndret) {
        if (meldekortEvent.erArbeidssokerNestePeriode()) {
            logger.info("Arbeidssøker ønsker å stå som aktiv også neste periode")
            return
        }
        logger.info("Avslutter arbeidssøkerperiode som følge av NEI i meldekortet")
        arbeidssoker.avsluttPeriode(meldekortEvent.opprettetTidspunkt())
    }
}

/**
 * Tidligere arbeidssøker betyr at du tidligere har vært arbeidssøker, men ikke er det lenger.
*/
private object TidligereArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker) {
        logger.info("Starter arbeidssøkerperiode som følge av ordinær registrering")

        if (kanSistePeriodeGjenåpnes(arbeidssoker, ordinaerBrukerRegistrering.opprettetTidspunkt().toLocalDate())) {
            logger.info("Reåpner tidligere arbeidssøkerperiode som følge av ordinær registrering")
            arbeidssoker.sistePeriode()!!.gjenåpne()
        } else {
            arbeidssoker.startPeriode(ordinaerBrukerRegistrering.opprettetTidspunkt())
        }
    }

    override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: ReaktiverArbeidssøker) {
        if (arbeidssoker.ikkeHarTidligerePerioder())
            throw IllegalStateException("Tilstanden er feil - TidligereArbeidssokerState skal alltid ha tidligere perioder.")

        if (arbeidssoker.harVærtInaktivMerEnn28Dager()) {
            logger.warn("Arbeidssøker har vært inaktiv mer enn 28 dager - kan derfor ikke reaktiveres")
            return
        }
        logger.info("Starter arbeidssøkerperiode som følge av reaktivering")
        arbeidssoker.startPeriode(reaktivering.opprettetTidspunkt())
    }

    override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndret) {
        if (!formidlingsgruppeEndretEvent.formidlingsgruppe().erArbeidssoker()) {
            logger.info("Avviser FormidlingsgruppeEndretEvent ${formidlingsgruppeEndretEvent.formidlingsgruppe()} - Arbeidssøker er allerede inaktiv")
            return
        }

        if (kanSistePeriodeGjenåpnes(arbeidssoker, formidlingsgruppeEndretEvent.opprettetTidspunkt().toLocalDate())) {
            logger.warn("Reåpner tidligere arbeidssøkerperiode som følge av formidlingsgruppe")
            arbeidssoker.sistePeriode()!!.gjenåpne()
        } else {
            logger.warn("Arbeidssøkerperioden ble initiert av en formidlingsgruppe - ikke en ordinær/reaktivert registrering")
            arbeidssoker.startPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())
        }
    }

    private fun kanSistePeriodeGjenåpnes(arbeidssoker: Arbeidssoker, registreringstidspunkt: LocalDate) =
        arbeidssoker.sistePeriode()!!.tilDato?.toLocalDate() == registreringstidspunkt

    override fun behandle(arbeidssoker: Arbeidssoker, meldekortEvent: MeldekortEndret) {
        if (!meldekortEvent.erArbeidssokerNestePeriode()) {
            logger.info("Avviser MeldekortEventArbeidssøker er allerede inaktiv")
            return
        }
        logger.warn("Arbeidssøkerperioden ble initiert av et meldekort - ikke en ordinær/reaktivert registrering")
        arbeidssoker.startPeriode(meldekortEvent.opprettetTidspunkt())
    }

}

