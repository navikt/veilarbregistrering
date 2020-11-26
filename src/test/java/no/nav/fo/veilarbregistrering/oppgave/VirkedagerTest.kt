package no.nav.fo.veilarbregistrering.oppgave

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class VirkedagerTest {
    @Test
    fun vanligMandag() {
        Assertions.assertThat(Virkedager.plussAntallArbeidsdager(LocalDate.of(2020, 5, 25), 3))
                .isEqualTo(LocalDate.of(2020, 5, 28))
    }

    @Test
    fun vanligTirsdag() {
        Assertions.assertThat(Virkedager.plussAntallArbeidsdager(LocalDate.of(2020, 5, 26), 3))
                .isEqualTo(LocalDate.of(2020, 5, 29))
    }

    @Test
    fun onsdagMedPåfølgendePinse() {
        Assertions.assertThat(Virkedager.plussAntallArbeidsdager(LocalDate.of(2020, 5, 27), 3))
                .isEqualTo(LocalDate.of(2020, 6, 2))
    }

    @Test
    fun vanligOnsdagMedHelgIMellom() {
        Assertions.assertThat(Virkedager.plussAntallArbeidsdager(LocalDate.of(2020, 6, 3), 3))
                .isEqualTo(LocalDate.of(2020, 6, 8))
    }
}