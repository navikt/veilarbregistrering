package no.nav.fo.veilarbregistrering.oppgave;

import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class HelligdagerTest {

    @Test
    public void nasjonaldagen_17_mai_2020_er_en_helligdag() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2020, 5,17))).isTrue();
    }

    @Test
    public void langfredag_10_april_2020_er_en_helligdag() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2020, 4,10))).isTrue();
    }

    @Test
    public void vanlig_dag_som_6_april_2020_er_ikke_en_helligdag() {
        assertThat(Helligdager.erHelligdag(LocalDate.of(2020, 4,6))).isFalse();
    }
}