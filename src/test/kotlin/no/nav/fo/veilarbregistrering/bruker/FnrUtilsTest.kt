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
    fun `skal være 20 år`() {
        assertAntallAar(20, LocalDate.of(1997, 12, 14))
        assertAntallAar(20, LocalDate.of(1997, 1, 1))
    }

    @Test
    fun `skal være 19 år`() {
        assertAntallAar(19, LocalDate.of(1997, 12, 15))
        assertAntallAar(19, LocalDate.of(1998, 12, 14))
    }

    @Test
    fun `skal være 1 år`() {
        assertAntallAar(1,
            dato = LocalDate.of(2016, 2, 29),
            dagensDato = LocalDate.of(2018, 2, 28))
    }

    @Test
    fun `skal være 2 år`() {
        assertAntallAar(2,
            dato = LocalDate.of(2016, 2, 29),
            dagensDato = LocalDate.of(2018, 3, 1)
            )
    }

    private fun assertAntallAar(antallAar: Int, dato: LocalDate, dagensDato: LocalDate = FnrUtilsTest.dagensDato) {
        assertThat(FnrUtils.antallAarSidenDato(dato, dagensDato)).isEqualTo(antallAar)
    }

    companion object {
        private val dagensDato = LocalDate.of(2017, 12, 14)
    }
}
