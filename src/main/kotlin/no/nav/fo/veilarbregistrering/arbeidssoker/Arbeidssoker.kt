package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.logger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Root aggregate - all kommunikasjon mot Arbeidssøker og underliggende elementer skal gå via dette objektet.
 */
class Arbeidssoker(private val foedselsnummer: Foedselsnummer): Observable {

    private val observers: MutableList<Observer> = mutableListOf()

    private var id: Int = 0
    private var tilstand: ArbeidssokerState = IkkeArbeidssokerState
    private var arbeidssokerperioder: MutableList<Arbeidssokerperiode> = mutableListOf()

    private fun id(): Int = ++id

    fun behandle(endreArbeidssøker: EndreArbeidssøker) {
        when (endreArbeidssøker) {
            is RegistrerArbeidssøker -> tilstand.behandle(this, endreArbeidssøker)
            is ReaktiverArbeidssøker -> tilstand.behandle(this, endreArbeidssøker)
            is FormidlingsgruppeEndret -> tilstand.behandle(this, endreArbeidssøker)
        }
    }

    private fun avsluttGammelOgStartNyPeriode(overgangsTidspunkt: LocalDateTime) {
        avsluttPeriode(overgangsTidspunkt)
        startPeriode(overgangsTidspunkt)
    }

    private fun startPeriode(fraDato: LocalDateTime) {
        this.arbeidssokerperioder.add(Arbeidssokerperiode(fraDato, null))
        this.observers.forEach { it.update(ArbeidssøkerperiodeStartetEvent(foedselsnummer, fraDato.toLocalDate()))}
    }

    private fun avsluttPeriode(tilDato: LocalDateTime) {
        val tilOgMedDato = atTheEndOfYesterday(tilDato)
        sistePeriode()?.avslutt(tilOgMedDato) ?: throw IllegalStateException("Kan ikke avslutte en periode som ikke finnes")
        this.observers.forEach { it.update(ArbeidssøkerperiodeAvsluttetEvent(foedselsnummer, tilOgMedDato.toLocalDate()))}
    }

    private fun atTheEndOfYesterday(localDateTime: LocalDateTime): LocalDateTime {
        return localDateTime.toLocalDate().atTime(23, 59, 59).minusDays(1)
    }

    private fun droppSistePeriode(opprettetTidspunkt: LocalDateTime) {
        arbeidssokerperioder.remove(sistePeriode())
        this.observers.forEach { it.update(ArbeidssøkerperiodeAvsluttetEvent(foedselsnummer, opprettetTidspunkt.toLocalDate()))}
    }

    private fun harVærtInaktivMerEnn28Dager() = sistePeriode()!!.tilDato!!.isBefore(LocalDateTime.now().minusDays(28))

    private fun ikkeHarTidligerePerioder(): Boolean = arbeidssokerperioder.isEmpty()

    private fun sistePeriodeStartetSammeDag(opprettetTidspunkt: LocalDateTime): Boolean =
        sistePeriode()!!.fraDato.toLocalDate() == opprettetTidspunkt.toLocalDate()

    internal fun sistePeriode(): Arbeidssokerperiode? {
        if (arbeidssokerperioder.isEmpty()) return null
        return arbeidssokerperioder.sortedBy { it.fraDato }.last()
    }

    internal fun allePerioder(): List<Arbeidssokerperiode> {
        return arbeidssokerperioder
    }

    private infix fun nyTilstand(tilstand: ArbeidssokerState) {
        this.tilstand = tilstand
    }

    override fun add(observer: Observer) {
        this.observers.add(observer)
    }

    override fun remove(observer: Observer) {
        this.observers.remove(observer)
    }

    /**
     * Ikke arbeidssøker betyr at du aldri har vært arbeidssøker.
     */
    private object IkkeArbeidssokerState : ArbeidssokerState {
        override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker) {
            logger.info("${arbeidssoker.id()} - Starter (${formater(ordinaerBrukerRegistrering.opprettetTidspunkt())}) " +
                    "arbeidssøkerperiode som følge av ordinær registrering")
            arbeidssoker.startPeriode(ordinaerBrukerRegistrering.opprettetTidspunkt())
            arbeidssoker nyTilstand AktivArbeidssokerState
        }

        override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: ReaktiverArbeidssøker) {
            logger.warn("${arbeidssoker.id()} - Arbeidssøker har ingen tidligere arbeidssøkerperioder - kan derfor ikke reaktiveres")
            return
        }

        override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndret) {
            if (!formidlingsgruppeEndretEvent.formidlingsgruppe().erArbeidssoker()) {
                logger.warn("${arbeidssoker.id()} - Forkaster formidlingsgruppeEndretEvent med " +
                        "${formidlingsgruppeEndretEvent.formidlingsgruppe()} da Arbeidssøker ikke har noe historikk")
                return
            }
            logger.info("${arbeidssoker.id()} - Starter (${formater(formidlingsgruppeEndretEvent.opprettetTidspunkt())}) " +
                    "arbeidssøkerperioden som følge av en formidlingsgruppe (ARBS)")
            arbeidssoker.startPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())
            arbeidssoker nyTilstand AktivArbeidssokerState
        }
    }

    /**
     * Aktiv arbeidssøker betyr at bruker har en åpen periode - at perioden ikke er avsluttet og at tildato er null.
     */
    private object AktivArbeidssokerState : ArbeidssokerState {
        override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker) {
            logger.warn("${arbeidssoker.id()} - Avviser OrdinaerBrukerRegistrering (${formater(ordinaerBrukerRegistrering.opprettetTidspunkt())}) " +
                    "- Arbeidssøker er allerede aktiv")
        }

        override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: ReaktiverArbeidssøker) {
            logger.warn("${arbeidssoker.id()} - Avviser Reaktivering (${formater(reaktivering.opprettetTidspunkt())}) " +
                    "- Arbeidssøker er allerede aktiv")
        }

        override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndret) {
            if (formidlingsgruppeEndretEvent.formidlingsgruppe().erArbeidssoker()) {
                if (arbeidssoker.sistePeriodeStartetSammeDag(formidlingsgruppeEndretEvent.opprettetTidspunkt())) {
                    logger.info("${arbeidssoker.id()} - Avviser formidlingsgruppeendretEvent (ARBS) fordi vi allerede har startet " +
                            "arbeidssøkerperiode samme dag ((${formater(formidlingsgruppeEndretEvent.opprettetTidspunkt())})")
                    return
                }
                logger.info("${arbeidssoker.id()} - Avslutter (${formater(formidlingsgruppeEndretEvent.opprettetTidspunkt())}) " +
                        "arbeidssøkerperiode, og starter samtidig en ny som følge av " +
                        "${formidlingsgruppeEndretEvent.formidlingsgruppe()} fordi arbeidssøker allerede var aktiv")
                arbeidssoker.avsluttGammelOgStartNyPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())
                arbeidssoker nyTilstand AktivArbeidssokerState

            } else {
                if (arbeidssoker.sistePeriodeStartetSammeDag(formidlingsgruppeEndretEvent.opprettetTidspunkt())) {
                    logger.warn("${arbeidssoker.id()} - Dropper siste periode som følge av at vi mottar " +
                            "${formidlingsgruppeEndretEvent.formidlingsgruppe()} samme dag " +
                            "${formater(formidlingsgruppeEndretEvent.opprettetTidspunkt())} som perioden ble startet.")
                    arbeidssoker.droppSistePeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())

                } else {
                    logger.info("${arbeidssoker.id()} - Avslutter (${formater(formidlingsgruppeEndretEvent.opprettetTidspunkt())}) " +
                            "arbeiddssøkerperiode som følge av ${formidlingsgruppeEndretEvent.formidlingsgruppe()}")
                    arbeidssoker.avsluttPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())
                }

                if (arbeidssoker.sistePeriode() == null) {
                    arbeidssoker nyTilstand IkkeArbeidssokerState
                } else {
                    arbeidssoker nyTilstand TidligereArbeidssokerState
                }
            }
        }
    }

    /**
     * Tidligere arbeidssøker betyr at du tidligere har vært arbeidssøker, men ikke er det lenger.
     */
    private object TidligereArbeidssokerState : ArbeidssokerState {
        override fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker) {
            logger.info("${arbeidssoker.id()} - Starter (${formater(ordinaerBrukerRegistrering.opprettetTidspunkt())}) " +
                    "arbeidssøkerperiode som følge av ordinær registrering")
            arbeidssoker.startPeriode(ordinaerBrukerRegistrering.opprettetTidspunkt())
            arbeidssoker nyTilstand AktivArbeidssokerState
        }

        override fun behandle(arbeidssoker: Arbeidssoker, reaktivering: ReaktiverArbeidssøker) {
            if (arbeidssoker.ikkeHarTidligerePerioder())
                throw IllegalStateException("${arbeidssoker.id()} - Tilstanden er feil - TidligereArbeidssokerState skal alltid ha tidligere perioder.")

            if (arbeidssoker.harVærtInaktivMerEnn28Dager()) {
                logger.warn("${arbeidssoker.id()} - Arbeidssøker har vært inaktiv mer enn 28 dager - kan derfor ikke reaktiveres")
                return
            }
            logger.info("${arbeidssoker.id()} - Starter (${formater(reaktivering.opprettetTidspunkt())}) arbeidssøkerperiode som følge av reaktivering")
            arbeidssoker.startPeriode(reaktivering.opprettetTidspunkt())
            arbeidssoker nyTilstand AktivArbeidssokerState
        }

        override fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndret) {
            if (!formidlingsgruppeEndretEvent.formidlingsgruppe().erArbeidssoker()) {
                logger.info("${arbeidssoker.id()} - Avviser FormidlingsgruppeEndretEvent ${formidlingsgruppeEndretEvent.formidlingsgruppe()} - Arbeidssøker er allerede inaktiv")
                return
            }

            logger.warn("${arbeidssoker.id()} - Starter (${formater(formidlingsgruppeEndretEvent.opprettetTidspunkt())}) " +
                    "arbeidssøkerperiode som følge av en formidlingsgruppe (ARBS)")
            arbeidssoker.startPeriode(formidlingsgruppeEndretEvent.opprettetTidspunkt())
            arbeidssoker nyTilstand AktivArbeidssokerState
        }
    }

    companion object {
        private fun formater(localDateTime: LocalDateTime): String =
            localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:MM:SS"))
    }
}

private interface ArbeidssokerState {
    fun behandle(arbeidssoker: Arbeidssoker, ordinaerBrukerRegistrering: RegistrerArbeidssøker)
    fun behandle(arbeidssoker: Arbeidssoker, reaktivering: ReaktiverArbeidssøker)
    fun behandle(arbeidssoker: Arbeidssoker, formidlingsgruppeEndretEvent: FormidlingsgruppeEndret)
}

