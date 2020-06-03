package no.nav.fo.veilarbregistrering.oppgave;

import java.time.LocalDate;
import java.util.function.Predicate;

public class OppgavePredicates {

    public static Predicate<OppgaveImpl> oppgaveAvType(OppgaveType oppgaveType) {
        return o -> o.getOppgavetype().equals(oppgaveType);
    }

    public static Predicate<OppgaveImpl> oppgaveOpprettetForMindreEnnToArbeidsdagerSiden(LocalDate dagensDato) {
        return o -> o.getOpprettet().erMindreEnnToArbeidsdagerSiden(dagensDato);
    }
}
