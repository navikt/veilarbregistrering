package no.nav.fo.veilarbregistrering.bruker

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class FnrUtilsTest {

    @Test
    fun `skal utlede korrekt fodselsdato`() {
        val fnr = FoedselsnummerTestdataBuilder.getFodselsnummerAsStringOnDateMinusYears(dagensDato, 40)
        assertThat(FnrUtils.utledFodselsdatoForFnr(fnr)).isEqualTo(LocalDate.of(1977, 12, 14))
    }

    @Test
    fun skalVaere20Aar() {
        val dato = LocalDate.of(1997, 12, 14)
        assertThat(FnrUtils.antallAarSidenDato(dato, dagensDato)).isEqualTo(20)
    }

    @Test
    fun skalVaere20Aar_2() {
        val dato = LocalDate.of(1997, 1, 1)
        assertThat(FnrUtils.antallAarSidenDato(dato, dagensDato)).isEqualTo(20)
    }

    @Test
    fun skalVaere19Aar() {
        val dato = LocalDate.of(1997, 12, 15)
        assertThat(FnrUtils.antallAarSidenDato(dato, dagensDato)).isEqualTo(19)
    }

    @Test
    fun skalVaere19Aar_2() {
        val dato = LocalDate.of(1998, 12, 14)
        assertThat(FnrUtils.antallAarSidenDato(dato, dagensDato)).isEqualTo(19)
    }

    @Test
    fun skalVaere1Aar() {
        val dato = LocalDate.of(2016, 2, 29)
        val dagensDato = LocalDate.of(2018, 2, 28)
        assertThat(FnrUtils.antallAarSidenDato(dato, dagensDato)).isEqualTo(1)
    }

    @Test
    fun skalVaere2Aar() {
        val dato = LocalDate.of(2016, 2, 29)
        val dagensDato = LocalDate.of(2018, 3, 1)
        assertThat(FnrUtils.antallAarSidenDato(dato, dagensDato)).isEqualTo(2)
    }

    companion object {
        private val dagensDato = LocalDate.of(2017, 12, 14)
    }
}
