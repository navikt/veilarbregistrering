package no.nav.fo.veilarbregistrering.oppgave

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UkedagTest {
    @Test
    fun `mandag er ikke helg`() {
        assertThat(Ukedag.erHelg(MANDAG)).isFalse()
    }

    @Test
    fun `tirsdag er ikke helg`() {
        assertThat(Ukedag.erHelg(TIRSDAG)).isFalse()
    }

    @Test
    fun `torsdag er ikke helg`() {
        assertThat(Ukedag.erHelg(TORSDAG)).isFalse()
    }

    @Test
    fun `lørdag er helg`() {
        assertThat(Ukedag.erHelg(LØRDAG)).isTrue()
    }

    @Test
    fun `søndag er helg`() {
        assertThat(Ukedag.erHelg(SØNDAG)).isTrue()
    }

    companion object {
        private val SØNDAG = LocalDate.of(2020, 3, 29)
        private val MANDAG = LocalDate.of(2020, 3, 30)
        private val TIRSDAG = LocalDate.of(2020, 3, 31)
        private val TORSDAG = LocalDate.of(2020, 4, 9)
        private val LØRDAG = LocalDate.of(2020, 4, 11)
    }
}