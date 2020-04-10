package no.nav.fo.veilarbregistrering.oppgave;

import java.time.LocalDate;
import java.util.function.Predicate;

public class OppgavePredicates {

    public static Predicate<OppgaveImpl> oppgaveAvTypeOppholdstillatelse() {
        return o -> o.getOppgavetype().equals(OppgaveType.OPPHOLDSTILLATELSE);
    }

    public static Predicate<OppgaveImpl> oppgaveOpprettetForMindreEnnToArbeidsdagerSiden(LocalDate dagensDato) {
        return o -> new OppgaveOpprettet(o.getOpprettet().toLocalDate())
                .erMindreEnnToArbeidsdagerSiden(dagensDato);
    }
}
