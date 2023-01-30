package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.log.logger
import java.time.LocalDateTime

/**
 * Root aggregate - all kommunikasjon mot Arbeidssøker og underliggende elementer skal gå via dette objektet.
 */
class Arbeidssoker {

    private var tilstand: ArbeidssokerState = IkkeArbeidssokerState
    private var arbeidssokerperioder: MutableList<Arbeidssokerperiode> = mutableListOf()

    fun behandle(endreArbeidssøker: EndreArbeidssøker) {
        when (endreArbeidssøker) {
            is RegistrerArbeidssøker -> tilstand.behandle(this, endreArbeidssøker)
            is ReaktiverArbeidssøker -> tilstand.behandle(this, endreArbeidssøker)
            is FormidlingsgruppeEndret -> tilstand.behandle(this, endreArbeidssøker)
        }
    }

    internal fun avsluttGammelOgStartNyPeriode(overgangsTidspunkt: LocalDateTime) {
        avsluttPeriode(overgangsTidspunkt)
        startPeriode(overgangsTidspunkt)
    }

    internal fun startPeriode(fraDato: LocalDateTime) {
        this.arbeidssokerperioder.add(Arbeidssokerperiode(fraDato, null))
        this.tilstand = AktivArbeidssokerState
    }

    internal fun avsluttPeriode(tilDato: LocalDateTime) {
        sistePeriode()?.avslutt(tilDato) ?: throw IllegalStateException("Kan ikke avslutte en periode som ikke finnes")
        this.tilstand = TidligereArbeidssokerState
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

    fun droppSistePeriode() {
        arbeidssokerperioder.remove(sistePeriode())
    }
}

private interface ArbeidssokerState {
    fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker)
    fun behandle(arbeidssoker: Arbeidssoker, reaktivering: ReaktiverArbeidssøker)
    fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndret)
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
            logger.info("Avslutter arbeidssøkerperiode, og starter samtidig en ny som følge av " +
                    "${formidlingsgruppeEndretEvent.formidlingsgruppe()} fordi arbeidssøker allerede var aktiv")
            arbeidssoker.avsluttGammelOgStartNyPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())

        } else {
            if (arbeidssoker.sistePeriode()!!.fraDato.toLocalDate() == formidlingsgruppeEndretEvent.opprettetTidspunkt().toLocalDate()) {
                logger.warn("Dropper siste periode som følge av at vi mottar " +
                        "${formidlingsgruppeEndretEvent.formidlingsgruppe()} samme dag som perioden ble startet.")
                arbeidssoker.droppSistePeriode()

            } else {
                logger.info("Avslutter arbeiddssøkerperiode som følge av ${formidlingsgruppeEndretEvent.formidlingsgruppe()}")
                arbeidssoker.avsluttPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())
            }
        }
    }
}

/**
 * Tidligere arbeidssøker betyr at du tidligere har vært arbeidssøker, men ikke er det lenger.
 */
private object TidligereArbeidssokerState : ArbeidssokerState {
    override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker) {
        logger.info("Starter arbeidssøkerperiode som følge av ordinær registrering")
        arbeidssoker.startPeriode(ordinaerBrukerRegistrering.opprettetTidspunkt())
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

        if (arbeidssoker.sistePeriode()!!.tilDato!!.toLocalDate() == formidlingsgruppeEndretEvent.opprettetTidspunkt().toLocalDate()) {
            logger.info("Arbeidssøkerperiode ble endret samme dag. Forrige periode blir derfor negativ for at ikke tilDato på forrige periode skal være lik fraDato på neste.")
            arbeidssoker.sistePeriode()!!.korrigerForNegativPeriode()
        }
        logger.warn("Arbeidssøkerperioden ble initiert av en formidlingsgruppe - ikke en ordinær/reaktivert registrering")
        arbeidssoker.startPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())
    }
}

