package no.nav.fo.veilarbregistrering.bruker

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PeriodeTest {

    @Test
    fun `fraDatoAs yyyyMMdd skal skrive ut fradato pa formatet yyyyMMdd`() {
        val periode = Periode(LocalDate.of(2020, 1, 12), LocalDate.of(2020, 2, 20))
        assertThat(periode.fraDatoSomUtcString()).isEqualTo("2020-01-12")
    }

    @Test
    fun `tilDatoAs yyyyMMdd skal skrive ut tildato pa formatet yyyyMMdd`() {
        val periode = Periode(LocalDate.of(2020, 1, 12), LocalDate.of(2020, 2, 20))
        assertThat(periode.tilDatoSomUtcString()).isEqualTo("2020-02-20")
    }

    @Test
    fun `tildato er innenfor forespurt periode`() {
        // [januar februar mars april]
        // -------[februar mars april mai]
        val periodeMedTildato = Periode(FØRSTE_JANUAR, FØRSTE_APRIL)
        val forespurtPeriode = Periode(FØRSTE_FEBRUAR, FØRSTE_MAI)
        assertThat(periodeMedTildato.overlapperMed(forespurtPeriode)).isTrue
    }

    @Test
    fun `periode er avs`() {
        val periodeMedTildato = Periode(FØRSTE_JANUAR, LocalDate.of(2020, 1, 31))
        val forespurtPeriode = Periode(FØRSTE_FEBRUAR, FØRSTE_MAI)
        assertThat(periodeMedTildato.overlapperMed(forespurtPeriode)).isFalse
    }

    @Test
    fun `periode ar avs`() {
        val periodeMedTildato = Periode(LocalDate.of(2020, 6, 1), LocalDate.of(2020, 8, 31))
        val forespurtPeriode = Periode(FØRSTE_FEBRUAR, FØRSTE_MAI)
        assertThat(periodeMedTildato.overlapperMed(forespurtPeriode)).isFalse
    }

    @Test
    fun `forespurtPeriode med åpen tildato`() {
        val periodeMedTildato = Periode(FØRSTE_JANUAR, FØRSTE_APRIL)
        val forespurtPeriode = Periode(FØRSTE_FEBRUAR, null)
        assertThat(periodeMedTildato.overlapperMed(forespurtPeriode)).isTrue
    }

    @Test
    fun `periode med åpen tildato`() {
        val periodeMedTildato = Periode(FØRSTE_JANUAR, null)
        val forespurtPeriode = Periode(FØRSTE_FEBRUAR, FØRSTE_APRIL)
        assertThat(periodeMedTildato.overlapperMed(forespurtPeriode)).isTrue
    }

    companion object {
        private val FØRSTE_JANUAR = LocalDate.of(2020, 1, 1)
        private val FØRSTE_FEBRUAR = LocalDate.of(2020, 2, 1)
        private val FØRSTE_APRIL = LocalDate.of(2020, 4, 1)
        private val FØRSTE_MAI = LocalDate.of(2020, 5, 1)
    }
}
