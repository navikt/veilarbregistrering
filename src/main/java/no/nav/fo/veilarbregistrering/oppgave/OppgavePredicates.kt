package no.nav.fo.veilarbregistrering.oppgave

import java.time.LocalDate
import java.util.function.Predicate

object OppgavePredicates {
    fun oppgaveAvType(oppgaveType: OppgaveType): Predicate<OppgaveImpl> {
        return Predicate { o: OppgaveImpl -> o.oppgavetype == oppgaveType }
    }

    fun oppgaveOpprettetForMindreEnnToArbeidsdagerSiden(dagensDato: LocalDate): Predicate<OppgaveImpl> {
        return Predicate { o: OppgaveImpl -> o.opprettet.erMindreEnnToArbeidsdagerSiden(dagensDato) }
    }
}