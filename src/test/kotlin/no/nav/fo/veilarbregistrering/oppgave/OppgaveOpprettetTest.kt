package no.nav.fo.veilarbregistrering.oppgave

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.time.LocalDate

class OppgaveOpprettetTest {
    @Test // MANDAG - MANDAG => true
    fun mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_lik_oppgave_dato() {
        val oppgavenBleOpprettet = OppgaveOpprettet(MANDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(MANDAG_1)).isTrue()
    }

    @Test // MANDAG -> TIRSDAG -> true
    fun mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_en_dag_etter_oppgave_dato() {
        val oppgavenBleOpprettet = OppgaveOpprettet(MANDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TIRSDAG_1)).isTrue()
    }

    @Test // MANDAG -> TIRSDAG (1) -> ONSDAG
    fun mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_to_dager_etter_oppgave_dato() {
        val oppgavenBleOpprettet = OppgaveOpprettet(MANDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(ONSDAG_1)).isTrue()
    }

    @Test // MANDAG -> TIRSDAG (1), ONSDAG (2) -> TORSDAG
    fun mindreEnnToArbeidsdagerSiden_er_false_når_dagensdato_er_tre_dager_etter_oppgave_dato() {
        val oppgavenBleOpprettet = OppgaveOpprettet(MANDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TORDAG_1)).isFalse()
    }

    @Test // TORSDAG -> FREDAG (1) -> LØRDAG
    fun mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_lørdag() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(LØRDAG_1)).isTrue()
    }

    @Test // TORSDAG -> FREDAG (1), LØRDAG (x) -> SØNDAG
    fun mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_søndag() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(SØNDAG_1)).isTrue()
    }

    @Test // TORSDAG -> FREDAG (1), LØRDAG (x), SØNDAG (x) -> MANDAG
    fun mindreEnnToArbeidsdagerSiden_er_true_når_det_har_vært_helg() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(MANDAG_2)).isTrue()
    }

    @Test // TORSDAG -> FREDAG (1), LØRDAG (x), SØNDAG (x), MANDAG (2) -> TIRSDAG
    fun mindreEnnToArbeidsdagerSiden_er_false_når_det_har_vært_helg_og_en_dag() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORDAG_1.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TIRSDAG_2)).isFalse()
    }

    @Test // ONSDAG -> TORSDAG (x) FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x) -> TIRSDAG
    fun mindreEnnToArbeidsdagerSiden_er_true_når_det_har_vært_helg_og_helligdag() {
        val oppgavenBleOpprettet = OppgaveOpprettet(ONSDAG_2.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TIRSDAG_3)).isTrue()
    }

    @Test // ONSDAG -> TORSDAG (x) FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (1) -> ONSDAG
    fun mindreEnnToArbeidsdagerSiden_er_true_når_det_har_vært_helg_helligdag_og_en_dag() {
        val oppgavenBleOpprettet = OppgaveOpprettet(ONSDAG_2.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(ONSDAG_3)).isTrue()
    }

    @Test // ONSDAG -> TORSDAG (x), FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (1), ONSDAG (2) -> TORSDAG
    fun mindreEnnToArbeidsdagerSiden_gitt_to_helligdag_helg_helligdag_og_to_dager() {
        val oppgavenBleOpprettet = OppgaveOpprettet(ONSDAG_2.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TORSDAG_3)).isFalse()
    }

    @Test // TORSDAG -> FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (1), ONSDAG (2) -> TORSDAG
    fun mindreEnnToArbeidsdagerSiden_gitt_helligdag_helg_helligdag_og_to_dager() {
        val oppgavenBleOpprettet = OppgaveOpprettet(TORSDAG_HELLIGDAG.atStartOfDay())
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TORSDAG_3)).isFalse()
    }

    @Test // TIRSDAG -> ONSDAG (1), TORSDAG (x), FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (2) -> ONSDAG
    fun mindreEnnToArbeidsdagerSiden_gitt_en_arbeidsdag_helligdag_helg_helligdag_og_en_arbeidsdager() {
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