package no.nav.fo.veilarbregistrering.oppgave;

import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class UkedagTest {

    private static final LocalDate SØNDAG = LocalDate.of(2020, 3, 29);
    private static final LocalDate MANDAG = LocalDate.of(2020, 3, 30);
    private static final LocalDate TIRSDAG = LocalDate.of(2020, 3, 31);
    private static final LocalDate TORSDAG = LocalDate.of(2020, 4, 9);
    private static final LocalDate LØRDAG = LocalDate.of(2020, 4, 11);

    @Test
    public void mandag_er_ikke_helg() {
        assertThat(Ukedag.erHelg(MANDAG)).isFalse();
    }

    @Test
    public void tirsdag_er_ikke_helg() {
        assertThat(Ukedag.erHelg(TIRSDAG)).isFalse();
    }

    @Test
    public void torsdag_er_ikke_helg() {
        assertThat(Ukedag.erHelg(TORSDAG)).isFalse();
    }

    @Test
    public void lørdag_er_helg() {
        assertThat(Ukedag.erHelg(LØRDAG)).isTrue();
    }

    @Test
    public void søndag_er_helg() {
        assertThat(Ukedag.erHelg(SØNDAG)).isTrue();
    }



}
