package no.nav.fo.veilarbregistrering.oppgave

import org.assertj.core.api.Assertions
import org.junit.Test
import java.time.LocalDate

class UkedagTest {
    @Test
    fun mandag_er_ikke_helg() {
        Assertions.assertThat(Ukedag.erHelg(MANDAG)).isFalse()
    }

    @Test
    fun tirsdag_er_ikke_helg() {
        Assertions.assertThat(Ukedag.erHelg(TIRSDAG)).isFalse()
    }

    @Test
    fun torsdag_er_ikke_helg() {
        Assertions.assertThat(Ukedag.erHelg(TORSDAG)).isFalse()
    }

    @Test
    fun lørdag_er_helg() {
        Assertions.assertThat(Ukedag.erHelg(LØRDAG)).isTrue()
    }

    @Test
    fun søndag_er_helg() {
        Assertions.assertThat(Ukedag.erHelg(SØNDAG)).isTrue()
    }

    companion object {
        private val SØNDAG = LocalDate.of(2020, 3, 29)
        private val MANDAG = LocalDate.of(2020, 3, 30)
        private val TIRSDAG = LocalDate.of(2020, 3, 31)
        private val TORSDAG = LocalDate.of(2020, 4, 9)
        private val LØRDAG = LocalDate.of(2020, 4, 11)
    }
}