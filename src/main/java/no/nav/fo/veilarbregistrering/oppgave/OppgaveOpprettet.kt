package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.metrics.Metric
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Inneholder dato for når oppgaven ble opprettet, med tilhørende logikk.
 */
internal class OppgaveOpprettet(val tidspunkt: LocalDateTime) : Metric {
    /**
     * Returnerer true dersom det er mindre enn to arbeidsdager (ikke helg (lørdag/søndag) eller
     * helligdag) mellom når oppgaven ble opprettet og dagens dato. Regner ikke med når på dagen
     * oppgaven ble opprettet.
     */
    fun erMindreEnnToArbeidsdagerSiden(dagensDato: LocalDate): Boolean {
        val toArbeidsdagerEtterOppgavenBleOpprettet = Virkedager.plussAntallArbeidsdager(
            tidspunkt.toLocalDate(), 2
        )
        return dagensDato.isBefore(toArbeidsdagerEtterOppgavenBleOpprettet) || dagensDato.isEqual(
            toArbeidsdagerEtterOppgavenBleOpprettet
        )
    }

    fun antallTimerSiden(): Long {
        return ChronoUnit.HOURS.between(tidspunkt, LocalDateTime.now())
    }

    override fun fieldName(): String {
        return "timer"
    }

    override fun value(): Long {
        return antallTimerSiden()
    }
}