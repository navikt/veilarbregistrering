package no.nav.fo.veilarbregistrering.oppgave

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class HelligdagerTest {
    @Test
    fun `nasjonaldagen 17 mai 2020 er en helligdag`() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2020, 5, 17))).isTrue()
    }

    @Test
    fun `langfredag 10 april 2020 er en helligdag`() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2020, 4, 10))).isTrue()
    }

    @Test
    fun `vanlig dag som 6 april 2020 er ikke en helligdag`() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2020, 4, 6))).isFalse()
    }

    @Test
    fun `nasjonaldagen 17 mai 2021 er en helligdag`() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2021, 5, 17))).isTrue()
    }

    @Test
    fun `langfredag 10 april 2021 er en helligdag`() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2021, 4, 2))).isTrue()
    }

    @Test
    fun `vanlig dag som 6 april 2021 er ikke en helligdag`() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2021, 4, 6))).isFalse()
    }
}