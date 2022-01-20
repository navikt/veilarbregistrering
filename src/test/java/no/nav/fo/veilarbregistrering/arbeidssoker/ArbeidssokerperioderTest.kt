package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeTestdataBuilder.Companion.medArbs
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeTestdataBuilder.Companion.medIserv
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperioderTestdataBuilder.Companion.arbeidssokerperioder
import no.nav.fo.veilarbregistrering.bruker.Periode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidssokerperioderTest {
    @Test
    fun `gitt at forespurt periode starter etter eldste periode dekkes hele`() {
        val arbeidssokerperioder = Arbeidssokerperioder(
            listOf(
                ARBEIDSSOKERPERIODE_2
            )
        )
        val forespurtPeriode = Periode.of(
            LocalDate.of(2020, 2, 1),
            null
        )
        assertThat(arbeidssokerperioder.dekkerHele(forespurtPeriode)).isTrue
    }

    @Test
    fun `gitt at forespurt periode starter før eldste periode dekkes ikke hele`() {
        val arbeidssokerperioder = Arbeidssokerperioder(
            listOf(
                ARBEIDSSOKERPERIODE_2
            )
        )
        val forespurtPeriode = Periode.of(
            LocalDate.of(2019, 2, 1),
            null
        )
        assertThat(arbeidssokerperioder.dekkerHele(forespurtPeriode)).isFalse
    }

    @Test
    fun `gitt at forespurt periode starter samme dag som eldste periode dekkes hele perioden`() {
        val arbeidssokerperioder = Arbeidssokerperioder(
            listOf(
                ARBEIDSSOKERPERIODE_2
            )
        )
        val forespurtPeriode = Periode.of(
            LocalDate.of(2020, 1, 1),
            null
        )
        assertThat(arbeidssokerperioder.dekkerHele(forespurtPeriode)).isTrue
    }

    @Test
    fun `gitt at forespurt periode slutter dagen etter siste periode`() {
        val arbeidssokerperioder = Arbeidssokerperioder(
            listOf(
                ARBEIDSSOKERPERIODE_1
            )
        )
        val forespurtPeriode = Periode.of(
            LocalDate.of(2016, 10, 1),
            LocalDate.of(2020, 6, 25)
        )
        assertThat(arbeidssokerperioder.dekkerHele(forespurtPeriode)).isTrue
    }

    @Test
    fun `gitt flere perioder skal de periodene hvor en er arbs returneres`() {
        val arbeidssokerperioder = arbeidssokerperioder()
            .periode(
                medArbs()
                    .fra(LocalDate.of(2020, 3, 19))
                    .til(LocalDate.of(2020, 4, 20))
            )
            .periode(
                medIserv()
                    .fra(LocalDate.of(2020, 4, 21))
                    .til(LocalDate.of(2020, 4, 29))
            )
            .periode(
                medArbs()
                    .fra(LocalDate.of(2020, 4, 30))
            )
            .build()
        val arbeidssokerperiodes = arbeidssokerperioder.overlapperMed(
            Periode.of(
                LocalDate.of(2020, 4, 13),
                LocalDate.of(2020, 6, 28)
            )
        )
        assertThat(arbeidssokerperiodes.asList()).hasSize(2)
    }

    companion object {
        private val ARBEIDSSOKERPERIODE_1 = Arbeidssokerperiode(
            Formidlingsgruppe.of("ISERV"),
            Periode.of(LocalDate.of(2016, 9, 24), null)
        )
        private val ARBEIDSSOKERPERIODE_2 = Arbeidssokerperiode(
            Formidlingsgruppe.of("ARBS"),
            Periode.of(LocalDate.of(2020, 1, 1), null)
        )
    }
}
