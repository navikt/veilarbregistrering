package no.nav.fo.veilarbregistrering.oppgave;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class OppgavePredicates {

    public static Predicate<OppgaveImpl> oppgaveAvTypeOppholdstillatelse() {
        return o -> o.getOppgavetype().equals(OppgaveType.OPPHOLDSTILLATELSE);
    }

    public static Predicate<OppgaveImpl> oppgaveOpprettetForMindreEnnToArbeidsdagerSiden(LocalDateTime dagensDato) {
        return o -> o.getOpprettet().erMindreEnnToArbeidsdagerSiden(dagensDato);
    }
}
