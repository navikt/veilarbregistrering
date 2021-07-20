package no.nav.fo.veilarbregistrering.arbeidsforhold

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdataBuilder.Companion.medDato
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidsforholdTest {
    @Test
    fun `dato skal være innefor periode naar tom er null`() {
        val mnd = LocalDate.of(2017, 12, 1)
        val fom = LocalDate.of(2010, 12, 1)
        val arbeidsforhold = medDato(fom, null)
        assertThat(arbeidsforhold.erDatoInnenforPeriode(mnd)).isTrue
    }

    @Test
    fun `dato skal være innefor periode`() {
        val mnd = LocalDate.of(2017, 12, 1)
        val fom = LocalDate.of(2017, 12, 1)
        val tom = LocalDate.of(2017, 12, 30)
        val arbeidsforhold = medDato(fom, tom)
        assertThat(arbeidsforhold.erDatoInnenforPeriode(mnd)).isTrue
    }

    @Test
    fun `dato skal være innefor periode 2`() {
        val mnd = LocalDate.of(2017, 12, 1)
        val fom = LocalDate.of(2017, 10, 1)
        val tom = LocalDate.of(2017, 12, 1)
        val arbeidsforhold = medDato(fom, tom)
        assertThat(arbeidsforhold.erDatoInnenforPeriode(mnd)).isTrue
    }

    @Test
    fun `dato skal ikke være innefor periode`() {
        val mnd = LocalDate.of(2017, 12, 1)
        val fom = LocalDate.of(2017, 9, 1)
        val tom = LocalDate.of(2017, 11, 30)
        val arbeidsforhold = medDato(fom, tom)
        assertThat(arbeidsforhold.erDatoInnenforPeriode(mnd)).isFalse
    }
}
