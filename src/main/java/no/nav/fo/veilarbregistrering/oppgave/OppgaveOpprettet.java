package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.time.LocalDate;
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

    /**
     * Returnerer true dersom det er mindre enn to arbeidsdager (ikke helg (lørdag/søndag) eller
     * helligdag) mellom når oppgaven ble opprettet og dagens dato. Regner ikke med når på dagen
     * oppgaven ble opprettet.
     */
    boolean erMindreEnnToArbeidsdagerSiden(LocalDate dagensDato) {
        int antallArbeidsdager = 0;

        for (LocalDate date = oppgaveOpprettet.toLocalDate().plusDays(1); date.isBefore(dagensDato); date = date.plusDays(1)) {
            if (Ukedag.erHelg(date)) {
                continue;
            }

            if (Helligdager.erHelligdag(date)) {
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
