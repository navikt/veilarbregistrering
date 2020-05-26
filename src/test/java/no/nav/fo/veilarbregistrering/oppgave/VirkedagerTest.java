package no.nav.fo.veilarbregistrering.oppgave;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class VirkedagerTest {

    @Test
    public void vanligMandag() {
        assertThat(Virkedager.plussAntallArbeidsdager(LocalDate.of(2020, 5, 25), 3))
                .isEqualTo(LocalDate.of(2020, 5, 28));
    }

    @Test
    public void vanligTirsdag() {
        assertThat(Virkedager.plussAntallArbeidsdager(LocalDate.of(2020, 5, 26), 3))
                .isEqualTo(LocalDate.of(2020, 5, 29));
    }

    @Test
    public void onsdagMedPåfølgendePinse() {
        assertThat(Virkedager.plussAntallArbeidsdager(LocalDate.of(2020, 5, 27), 3))
                .isEqualTo(LocalDate.of(2020, 6, 2));
    }

    @Test
    public void vanligOnsdagMedHelgIMellom() {
        assertThat(Virkedager.plussAntallArbeidsdager(LocalDate.of(2020, 6, 3), 3))
                .isEqualTo(LocalDate.of(2020, 6, 8));
    }
}
