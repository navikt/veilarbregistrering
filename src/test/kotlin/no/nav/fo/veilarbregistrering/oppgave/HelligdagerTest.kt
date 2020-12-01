package no.nav.fo.veilarbregistrering.oppgave

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class HelligdagerTest {
    @Test
    fun nasjonaldagen_17_mai_2020_er_en_helligdag() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2020, 5, 17))).isTrue()
    }

    @Test
    fun langfredag_10_april_2020_er_en_helligdag() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2020, 4, 10))).isTrue()
    }

    @Test
    fun vanlig_dag_som_6_april_2020_er_ikke_en_helligdag() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2020, 4, 6))).isFalse()
    }
}