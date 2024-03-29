package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerperiodeTestdataBuilder.Companion.arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerperioderTestdataBuilder.Companion.arbeidssokerperioder
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
        val forespurtPeriode = Periode(
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
        val forespurtPeriode = Periode(
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
        val forespurtPeriode = Periode(
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
        val forespurtPeriode = Periode(
            LocalDate.of(2016, 10, 1),
            LocalDate.of(2020, 6, 25)
        )
        assertThat(arbeidssokerperioder.dekkerHele(forespurtPeriode)).isTrue
    }

    @Test
    fun `gitt flere perioder, skal kun de som overlapper med forespurt periode returneres`() {
        val arbeidssokerperioder = arbeidssokerperioder()
            .periode(
                arbeidssokerperiode()
                    .fra(LocalDate.of(2020, 1, 19))
                    .til(LocalDate.of(2020, 2, 20))
            )
            .periode(
                arbeidssokerperiode()
                    .fra(LocalDate.of(2020, 3, 19))
                    .til(LocalDate.of(2020, 4, 20))
            )
            .periode(
                arbeidssokerperiode()
                    .fra(LocalDate.of(2020, 4, 21))
                    .til(LocalDate.of(2020, 4, 29))
            )
            .periode(
                arbeidssokerperiode()
                    .fra(LocalDate.of(2020, 4, 30))
            )
            .periode(
                arbeidssokerperiode()
                    .fra(LocalDate.of(2020, 8, 30))
            )
            .build()
        val arbeidssokerperiodes = arbeidssokerperioder.overlapperMed(
            Periode(
                LocalDate.of(2020, 4, 13),
                LocalDate.of(2020, 6, 28)
            )
        )
        assertThat(arbeidssokerperiodes.asList()).hasSize(3)
    }

    companion object {
        private val ARBEIDSSOKERPERIODE_1 = Arbeidssokerperiode(
            Periode(LocalDate.of(2016, 9, 24), null)
        )
        private val ARBEIDSSOKERPERIODE_2 = Arbeidssokerperiode(
            Periode(LocalDate.of(2020, 1, 1), null)
        )
    }
}
