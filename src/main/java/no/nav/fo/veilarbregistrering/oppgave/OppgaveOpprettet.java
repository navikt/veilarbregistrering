package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Inneholder dato for når oppgaven ble opprettet, med tilhørende logikk.
 */
class OppgaveOpprettet implements Metric {

    private final LocalDateTime oppgaveOpprettet;

    public OppgaveOpprettet(LocalDateTime oppgaveOpprettet) {
        this.oppgaveOpprettet = oppgaveOpprettet;
    }

    boolean erMindreEnnToArbeidsdagerSiden(LocalDateTime dagensDato) {
        int antallArbeidsdager = 0;

        for (LocalDateTime date = oppgaveOpprettet.plusDays(1); date.isBefore(dagensDato); date = date.plusDays(1)) {
            if (Ukedag.erHelg(date.toLocalDate())) {
                continue;
            }

            if (Helligdager.erHelligdag(date.toLocalDate())) {
                continue;
            }

            antallArbeidsdager++;

            if (antallArbeidsdager == 2) {
                return false;
            }
        }

        return antallArbeidsdager < 2;
    }

    long antallTimerSiden() {
        return ChronoUnit.HOURS.between(this.oppgaveOpprettet, LocalDateTime.now());
    }

    LocalDateTime tidspunkt() {
        return oppgaveOpprettet;
    }

    @Override
    public String fieldName() {
        return "timer";
    }

    @Override
    public Long value() {
        return antallTimerSiden();
    }
}
