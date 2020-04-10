package no.nav.fo.veilarbregistrering.oppgave;

import java.time.LocalDate;
import java.util.function.Predicate;

/**
 * Inneholder dato for når oppgaven ble opprettet, med tilhørende logikk.
 */
class OppgaveOpprettet {

    private final LocalDate dato;

    OppgaveOpprettet(LocalDate dato) {
        this.dato = dato;
    }

    boolean erMindreEnnToArbeidsdagerSiden(LocalDate dagensDato) {
        int antallArbeidsdager = 0;

        for (LocalDate date = dato.plusDays(1); date.isBefore(dagensDato); date = date.plusDays(1)) {
            if (Ukedag.erHelg(date)) {
                continue;
            }

            if (Helligdager.erHelligdag(date)) {
                continue;
            }

            antallArbeidsdager++;

            // for å unngå og loope mer enn nødvendig, så stopper vi på maks 5 iterasjoner
            if (antallArbeidsdager == 5) {
                return false;
            }
        }

        return antallArbeidsdager < 2;
    }
}
