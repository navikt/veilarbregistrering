package no.nav.fo.veilarbregistrering.oppgave

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class OppgaveOpprettetTest {
    @Test // MANDAG - MANDAG => true
    fun `mindreEnnToArbeidsdagerSiden er true når dagensdato er lik oppgave dato`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(MANDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(MANDAG_1)).isTrue()
    }

    @Test // MANDAG -> TIRSDAG -> true
    fun `mindreEnnToArbeidsdagerSiden er true når dagensdato er en dag etter oppgave dato`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(MANDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TIRSDAG_1)).isTrue()
    }

    @Test // MANDAG -> TIRSDAG (1) -> ONSDAG
    fun `mindreEnnToArbeidsdagerSiden er true når dagensdato er to dager etter oppgave dato`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(MANDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(ONSDAG_1)).isTrue()
    }

    @Test // MANDAG -> TIRSDAG (1), ONSDAG (2) -> TORSDAG
    fun `mindreEnnToArbeidsdagerSiden er false når dagensdato er tre dager etter oppgave dato`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(MANDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TORDAG_1)).isFalse()
    }

    @Test // TORSDAG -> FREDAG (1) -> LØRDAG
    fun `mindreEnnToArbeidsdagerSiden er true når dagensdato er lørdag`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(LØRDAG_1)).isTrue()
    }

    @Test // TORSDAG -> FREDAG (1), LØRDAG (x) -> SØNDAG
    fun `mindreEnnToArbeidsdagerSiden er true når dagensdato er søndag`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(SØNDAG_1)).isTrue()
    }

    @Test // TORSDAG -> FREDAG (1), LØRDAG (x), SØNDAG (x) -> MANDAG
    fun `mindreEnnToArbeidsdagerSiden er true når det har vært helg`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(MANDAG_2)).isTrue()
    }

    @Test // TORSDAG -> FREDAG (1), LØRDAG (x), SØNDAG (x), MANDAG (2) -> TIRSDAG
    fun `mindreEnnToArbeidsdagerSiden er false når det har vært helg og en dag`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TIRSDAG_2)).isFalse()
    }

    @Test // ONSDAG -> TORSDAG (x) FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x) -> TIRSDAG
    fun `mindreEnnToArbeidsdagerSiden er true når det har vært helg og helligdag`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(ONSDAG_2.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TIRSDAG_3)).isTrue()
    }

    @Test // ONSDAG -> TORSDAG (x) FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (1) -> ONSDAG
    fun `mindreEnnToArbeidsdagerSiden er true når det har vært helg helligdag og en dag`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(ONSDAG_2.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(ONSDAG_3)).isTrue()
    }

    @Test // ONSDAG -> TORSDAG (x), FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (1), ONSDAG (2) -> TORSDAG
    fun `mindreEnnToArbeidsdagerSiden gitt to helligdag helg helligdag og to dager`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(ONSDAG_2.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TORSDAG_3)).isFalse()
    }

    @Test // TORSDAG -> FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (1), ONSDAG (2) -> TORSDAG
    fun `mindreEnnToArbeidsdagerSiden gitt helligdag helg helligdag og to dager`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORSDAG_HELLIGDAG.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TORSDAG_3)).isFalse()
    }

    @Test // TIRSDAG -> ONSDAG (1), TORSDAG (x), FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (2) -> ONSDAG
    fun `mindreEnnToArbeidsdagerSiden gitt en arbeidsdag helligdag helg helligdag og en arbeidsdager`() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TIRSDAG_2.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(ONSDAG_3)).isFalse()
    }

    companion object {
        private val MANDAG_1 = LocalDate.of(2020, 3, 30)
        private val TIRSDAG_1 = LocalDate.of(2020, 3, 31)
        private val ONSDAG_1 = LocalDate.of(2020, 4, 1)
        private val TORDAG_1 = LocalDate.of(2020, 4, 2)
        private val LØRDAG_1 = LocalDate.of(2020, 4, 4)
        private val SØNDAG_1 = LocalDate.of(2020, 4, 5)
        private val MANDAG_2 = LocalDate.of(2020, 4, 6)
        private val TIRSDAG_2 = LocalDate.of(2020, 4, 7)
        private val ONSDAG_2 = LocalDate.of(2020, 4, 8)
        private val TORSDAG_HELLIGDAG = LocalDate.of(2020, 4, 9)
        private val TIRSDAG_3 = LocalDate.of(2020, 4, 14)
        private val ONSDAG_3 = LocalDate.of(2020, 4, 15)
        private val TORSDAG_3 = LocalDate.of(2020, 4, 16)
    }
}