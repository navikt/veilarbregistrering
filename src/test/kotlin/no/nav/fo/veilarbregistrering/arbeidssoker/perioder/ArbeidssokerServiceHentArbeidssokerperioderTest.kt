package no.nav.fo.veilarbregistrering.arbeidssoker.perioder


import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeService
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidssokerServiceHentArbeidssokerperioderTest {
    private lateinit var arbeidssokerService: ArbeidssokerService
    private val arbeidssokerperiodeService = mockk<ArbeidssokerperiodeService>(relaxed = true)

    @BeforeEach
    fun setup() {
        arbeidssokerService = ArbeidssokerService(
            arbeidssokerperiodeService
        )

        every { arbeidssokerperiodeService.hentPerioder(any()) } returns emptyList()
    }

    @Test
    fun `hentArbeidssokerperioder skal returnere perioder sortert etter fradato`() {
        val forespurtPeriode = Periode(
            LocalDate.of(2020, 1, 2),
            LocalDate.of(2020, 5, 1)
        )
        every { arbeidssokerperiodeService.hentPerioder(BRUKER_3.gjeldendeFoedselsnummer) } returns finnArbeissokerperioder(BRUKER_3.gjeldendeFoedselsnummer)
        val arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(BRUKER_3, forespurtPeriode)
        assertThat(arbeidssokerperiodes.eldsteFoerst()).containsExactly(
            ARBEIDSSOKERPERIODE_1,
            ARBEIDSSOKERPERIODE_2,
            ARBEIDSSOKERPERIODE_3,
            ARBEIDSSOKERPERIODE_4
        )
    }
    @Test
    fun `hentArbeidssokerperioder ingen treff på fnr skal returnere tom liste`() {
        val forespurtPeriode = Periode(
            LocalDate.of(2019, 5, 1),
            LocalDate.of(2019, 11, 30)
        )
        val arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(BRUKER_3, forespurtPeriode)
        assertThat(arbeidssokerperiodes.asList()).isEmpty()
    }

    private fun finnArbeissokerperioder(foedselsnummer: Foedselsnummer): List<Periode> {
        val map: Map<Foedselsnummer, List<Periode>> = mapOf(
            FOEDSELSNUMMER_3 to listOf(
                    ARBEIDSSOKERPERIODE_2.periode,
                    ARBEIDSSOKERPERIODE_4.periode,
                    ARBEIDSSOKERPERIODE_3.periode,
                    ARBEIDSSOKERPERIODE_0.periode,
                    ARBEIDSSOKERPERIODE_1.periode,
                    ARBEIDSSOKERPERIODE_5.periode,
                    ARBEIDSSOKERPERIODE_6.periode
            ),
            FOEDSELSNUMMER_4 to emptyList()
        )
        return map[foedselsnummer]!!
    }

    companion object {
        private val FOEDSELSNUMMER_3 = Foedselsnummer("22334455661")
        private val FOEDSELSNUMMER_4 = Foedselsnummer("99887766554")
        private val BRUKER_3 = Bruker(FOEDSELSNUMMER_3, AktorId("100002345678"), emptyList())
        val ARBEIDSSOKERPERIODE_0 = Arbeidssokerperiode(
            Periode(LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 31))
        )
        val ARBEIDSSOKERPERIODE_1 = Arbeidssokerperiode(
            Periode(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
        )
        val ARBEIDSSOKERPERIODE_2 = Arbeidssokerperiode(
            Periode(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 29))
        )
        val ARBEIDSSOKERPERIODE_3 = Arbeidssokerperiode(
            Periode(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
        )
        val ARBEIDSSOKERPERIODE_4 = Arbeidssokerperiode(
            Periode(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 2))
        )
        val ARBEIDSSOKERPERIODE_5 = Arbeidssokerperiode(
            Periode(LocalDate.of(2020, 5, 3), LocalDate.of(2020, 5, 9))
        )
        val ARBEIDSSOKERPERIODE_6 = Arbeidssokerperiode(
            Periode(LocalDate.of(2020, 5, 10), null)
        )
    }
}
