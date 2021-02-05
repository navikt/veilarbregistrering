package no.nav.fo.veilarbregistrering.sykemelding

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.Month

class MaksdatoTest {
    @Test
    fun skalVaereSykmeldtOverEllerLik39Uker() {
        val maksDato = "2018-10-01"
        val dagenDato = LocalDate.of(2018, Month.JUNE, 26)
        assertTrue(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato))
    }

    @Test
    fun skalVaereSykmeldtAkkurat52Uker() {
        val maksDato = "2018-12-11"
        val dagenDato = LocalDate.of(2018, Month.DECEMBER, 11)
        assertTrue(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato))
    }

    @Test
    fun skalVaereSykmeldtNesten52Uker() {
        val maksDato = "2018-12-11"
        val dagenDato = LocalDate.of(2018, Month.DECEMBER, 9)
        assertTrue(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato))
    }

    @Test
    fun skalIkkeVaereSykmeldtOver39Uker() {
        val maksDato = "2018-10-01"
        val dagenDato = LocalDate.of(2018, Month.APRIL, 9)
        assertFalse(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato))
    }

    @Test
    fun skalIkkeVaereSykmeldtOver39UkerNarMaksDatoErUnderDagensDato() {
        val maksDato = "2018-10-01"
        val dagenDato = LocalDate.of(2019, Month.APRIL, 9)
        assertFalse(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato))
    }

    @Test
    fun skalHandtereNullVedBeregnSykmeldtOver39uker() {
        val maksDato: String? = null
        val dagenDato = LocalDate.of(2019, Month.APRIL, 9)
        assertThrows<NullPointerException> { Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato) }
    }

    @Test
    fun antallUkerSykmeldt_er_negativt_mer_enn_ett_år_tidligere() {
        val dagenDato = LocalDate.of(2019, Month.JANUARY, 1)
        val maksdato = Maksdato.of("2020-03-01")
        val antallUkerSykmeldt = maksdato.antallUkerSykmeldt(dagenDato)
        assertThat(antallUkerSykmeldt).isEqualTo(-8)
    }

    @Test
    fun antallUkerSykmeldt_er_0_ved_start_sykepenger() {
        val dagenDato = LocalDate.of(2019, Month.MARCH, 1)
        val maksdato = Maksdato.of("2020-03-01")
        val antallUkerSykmeldt = maksdato.antallUkerSykmeldt(dagenDato)
        assertThat(antallUkerSykmeldt).isEqualTo(0)
    }

    @Test
    fun antallUkerSykmeldt_er_1_etter_en_uke_med_sykepenger() {
        val dagenDato = LocalDate.of(2019, Month.MARCH, 8)
        val maksdato = Maksdato.of("2020-03-01")
        val antallUkerSykmeldt = maksdato.antallUkerSykmeldt(dagenDato)
        assertThat(antallUkerSykmeldt).isEqualTo(1)
    }

    @Test
    fun antallUkerSykmeldt_er_52_når_dagens_dato_er_lik_maksdato() {
        val dagenDato = LocalDate.of(2020, Month.MARCH, 1)
        val maksdato = Maksdato.of("2020-03-01")
        val antallUkerSykmeldt = maksdato.antallUkerSykmeldt(dagenDato)
        assertThat(antallUkerSykmeldt).isEqualTo(52)
    }
}