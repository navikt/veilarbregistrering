package no.nav.fo.veilarbregistrering.oppgave;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class OppgaveOpprettetTest {

    private static final LocalDateTime MANDAG_1 = LocalDate.of(2020, 3, 30).atStartOfDay();
    private static final LocalDateTime TIRSDAG_1 = LocalDate.of(2020, 3, 31).atStartOfDay();
    private static final LocalDateTime ONSDAG_1 = LocalDate.of(2020, 4, 1).atStartOfDay();
    private static final LocalDateTime TORDAG_1 = LocalDate.of(2020, 4, 2).atStartOfDay();
    private static final LocalDateTime LØRDAG_1 = LocalDate.of(2020, 4, 4).atStartOfDay();
    private static final LocalDateTime SØNDAG_1 = LocalDate.of(2020, 4, 5).atStartOfDay();
    private static final LocalDateTime MANDAG_2 = LocalDate.of(2020, 4, 6).atStartOfDay();
    private static final LocalDateTime TIRSDAG_2 = LocalDate.of(2020, 4,7).atStartOfDay();
    private static final LocalDateTime ONSDAG_2 = LocalDate.of(2020, 4,8).atStartOfDay();
    private static final LocalDateTime TORSDAG_HELLIGDAG = LocalDate.of(2020, 4,9).atStartOfDay();
    private static final LocalDateTime TIRSDAG_3 = LocalDate.of(2020, 4,14).atStartOfDay();
    private static final LocalDateTime ONSDAG_3 = LocalDate.of(2020, 4,15).atStartOfDay();
    private static final LocalDateTime TORSDAG_3 = LocalDate.of(2020, 4,16).atStartOfDay();

    @Test // MANDAG - MANDAG => true
    public void mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_lik_oppgave_dato() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(MANDAG_1);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(MANDAG_1)).isTrue();
    }

    @Test // MANDAG -> TIRSDAG -> true
    public void mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_en_dag_etter_oppgave_dato() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(MANDAG_1);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TIRSDAG_1)).isTrue();
    }

    @Test // MANDAG -> TIRSDAG (1) -> ONSDAG
    public void mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_to_dager_etter_oppgave_dato() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(MANDAG_1);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(ONSDAG_1)).isTrue();
    }

    @Test // MANDAG -> TIRSDAG (1), ONSDAG (2) -> TORSDAG
    public void mindreEnnToArbeidsdagerSiden_er_false_når_dagensdato_er_tre_dager_etter_oppgave_dato() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(MANDAG_1);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TORDAG_1)).isFalse();
    }

    @Test // TORSDAG -> FREDAG (1) -> LØRDAG
    public void mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_lørdag() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(TORDAG_1);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(LØRDAG_1)).isTrue();
    }

    @Test // TORSDAG -> FREDAG (1), LØRDAG (x) -> SØNDAG
    public void mindreEnnToArbeidsdagerSiden_er_true_når_dagensdato_er_søndag() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(TORDAG_1);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(SØNDAG_1)).isTrue();
    }

    @Test // TORSDAG -> FREDAG (1), LØRDAG (x), SØNDAG (x) -> MANDAG
    public void mindreEnnToArbeidsdagerSiden_er_true_når_det_har_vært_helg() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(TORDAG_1);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(MANDAG_2)).isTrue();
    }

    @Test // TORSDAG -> FREDAG (1), LØRDAG (x), SØNDAG (x), MANDAG (2) -> TIRSDAG
    public void mindreEnnToArbeidsdagerSiden_er_false_når_det_har_vært_helg_og_en_dag() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(TORDAG_1);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TIRSDAG_2)).isFalse();
    }

    @Test // ONSDAG -> TORSDAG (x) FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x) -> TIRSDAG
    public void mindreEnnToArbeidsdagerSiden_er_true_når_det_har_vært_helg_og_helligdag() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(ONSDAG_2);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TIRSDAG_3)).isTrue();
    }

    @Test // ONSDAG -> TORSDAG (x) FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (1) -> ONSDAG
    public void mindreEnnToArbeidsdagerSiden_er_true_når_det_har_vært_helg_helligdag_og_en_dag() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(ONSDAG_2);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(ONSDAG_3)).isTrue();
    }

    @Test // ONSDAG -> TORSDAG (x), FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (1), ONSDAG (2) -> TORSDAG
    public void mindreEnnToArbeidsdagerSiden_gitt_to_helligdag_helg_helligdag_og_to_dager() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(ONSDAG_2);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TORSDAG_3)).isFalse();
    }

    @Test // TORSDAG -> FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (1), ONSDAG (2) -> TORSDAG
    public void mindreEnnToArbeidsdagerSiden_gitt_helligdag_helg_helligdag_og_to_dager() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(TORSDAG_HELLIGDAG);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(TORSDAG_3)).isFalse();
    }

    @Test // TIRSDAG -> ONSDAG (1), TORSDAG (x), FREDAG (x), LØRDAG (x), SØNDAG (x), MANDAG (x), TIRSDAG (2) -> ONSDAG
    public void mindreEnnToArbeidsdagerSiden_gitt_en_arbeidsdag_helligdag_helg_helligdag_og_en_arbeidsdager() {
        OppgaveOpprettet oppgavenBleOpprettet = new OppgaveOpprettet(TIRSDAG_2);
        assertThat(oppgavenBleOpprettet.erMindreEnnToArbeidsdagerSiden(ONSDAG_3)).isFalse();
    }

}
