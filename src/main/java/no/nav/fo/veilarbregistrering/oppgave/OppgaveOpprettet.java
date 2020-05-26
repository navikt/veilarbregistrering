package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.metrics.Metric;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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
        LocalDate localDate = Virkedager.plussAntallArbeidsdager(oppgaveOpprettet.toLocalDate(), 2);
        return Period.between(localDate, dagensDato).getDays() < 1;
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
