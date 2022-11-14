package no.nav.fo.veilarbregistrering.arbeidssoker


import io.mockk.every
import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidssokerServiceHentArbeidssokerperioderTest {
    private lateinit var arbeidssokerService: ArbeidssokerService
    private val unleashService = mockk<UnleashClient>()
    private val metricsService = mockk<MetricsService>(relaxed = true)

    @BeforeEach
    fun setup() {
        arbeidssokerService = ArbeidssokerService(
            StubFormidlingsgruppeRepository(),
            StubFormidlingsgruppeGateway(),
            unleashService,
            metricsService,
        )
    }

    @Test
    fun `hentArbeidssokerperioder skal returnere perioder sortert etter fradato`() {
        every { unleashService.isEnabled(ArbeidssokerService.VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE) } returns true

        val forespurtPeriode = Periode(
            LocalDate.of(2020, 1, 2),
            LocalDate.of(2020, 5, 1)
        )
        val arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(BRUKER_3, forespurtPeriode)
        assertThat(arbeidssokerperiodes.eldsteFoerst()).containsExactly(
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_1,
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_2,
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_3,
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_4
        )
    }

    @Test
    fun `hentArbeidssokerperioder skal hente fra ords`() {
        val forespurtPeriode = Periode(
            LocalDate.of(2019, 12, 1),
            LocalDate.of(2020, 5, 1)
        )
        val arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(BRUKER_3, forespurtPeriode)
        assertThat(arbeidssokerperiodes.eldsteFoerst()).containsExactly(
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_0,
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_1,
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_2,
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_3,
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_4
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

    @Test
    fun `hentArbeidssokerperioder ingen treff på bruker skal returnere tom liste`() {
        val forespurtPeriode = Periode(
            LocalDate.of(2019, 5, 1),
            LocalDate.of(2019, 11, 30)
        )
        val arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(BRUKER_1, forespurtPeriode)
        assertThat(arbeidssokerperiodes.asList()).isEmpty()
    }

    @Disabled
    @Test
    fun `hentArbeidssokerperioder skal returnere alle perioder for person innenfor forespurt periode lokalt`() {
        every {
            unleashService.isEnabled(ArbeidssokerService.VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)
        } returns true

        val bruker = Bruker.of(
            FOEDSELSNUMMER_3,
            AktorId("100002345678"),
            listOf(FOEDSELSNUMMER_2, FOEDSELSNUMMER_1)
        )
        val forespurtPeriode = Periode(
            LocalDate.of(2020, 3, 20),
            LocalDate.of(2020, 6, 10)
        )
        val arbeidssokerperioder = arbeidssokerService.hentArbeidssokerperioder(bruker, forespurtPeriode)
        assertThat(arbeidssokerperioder.eldsteFoerst()).containsExactly(
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_3,
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_4,
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_5,
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_6,
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_7
        )
    }

    @Test
    fun `hentArbeidssokerperioder skal returnere alle perioder for person innenfor forespurt periode ords`() {
        every {
            unleashService.isEnabled(ArbeidssokerService.VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)
        } returns true

        val forespurtPeriode = Periode(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 5, 9)
        )
        val arbeidssokerperioder = arbeidssokerService.hentArbeidssokerperioder(BRUKER_1, forespurtPeriode)
        assertThat(arbeidssokerperioder.eldsteFoerst()).containsExactly(
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_1,
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_2,
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_3,
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_4,
            StubFormidlingsgruppeGateway.ARBEIDSSOKERPERIODE_5
        )
    }

    @Disabled
    @Test
    fun `returnerer alle perioder (uten datocutoff) for person som berører forespurt periode`() {
        every {
            unleashService.isEnabled(ArbeidssokerService.VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)
        } returns true

        val forespurtPeriode = Periode(
            LocalDate.of(2021, 10, 1),
            LocalDate.of(2021, 10, 31)
        )
        val arbeidssokerperioder = arbeidssokerService.hentArbeidssokerperioder(BRUKER_2, forespurtPeriode)
        assertThat(arbeidssokerperioder.eldsteFoerst()).containsExactly(
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_9,
            StubFormidlingsgruppeRepository.ARBEIDSSOKERPERIODE_10,
        )
    }

    private class StubFormidlingsgruppeRepository : FormidlingsgruppeRepository {
        override fun lagre(event: FormidlingsgruppeEvent): Long {
            return 0
        }

        override fun finnFormidlingsgrupperOgMapTilArbeidssokerperioder(foedselsnummerList: List<Foedselsnummer>): Arbeidssokerperioder {
            return mapOf(
                FOEDSELSNUMMER_3 to Arbeidssokerperioder(
                    listOf(
                        ARBEIDSSOKERPERIODE_3,
                        ARBEIDSSOKERPERIODE_1,
                        ARBEIDSSOKERPERIODE_4,
                        ARBEIDSSOKERPERIODE_2,
                        ARBEIDSSOKERPERIODE_6,
                        ARBEIDSSOKERPERIODE_5,
                        ARBEIDSSOKERPERIODE_7,
                        ARBEIDSSOKERPERIODE_8,
                    )
                ),
                FOEDSELSNUMMER_4 to Arbeidssokerperioder(
                    listOf(
                        ARBEIDSSOKERPERIODE_9,
                        ARBEIDSSOKERPERIODE_10,
                    ),
                )
            ).entries.first { (fnr, _) -> fnr in foedselsnummerList }.value
        }

        companion object {
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
                Periode(LocalDate.of(2020, 5, 10), LocalDate.of(2020, 5, 29))
            )
            val ARBEIDSSOKERPERIODE_7 = Arbeidssokerperiode(
                Periode(LocalDate.of(2020, 5, 30), LocalDate.of(2020, 6, 30))
            )
            val ARBEIDSSOKERPERIODE_8 = Arbeidssokerperiode(
                Periode(LocalDate.of(2020, 7, 1), null)
            )
            val ARBEIDSSOKERPERIODE_9 = Arbeidssokerperiode(
                Periode(LocalDate.of(2021, 1, 15), LocalDate.of(2021, 10, 5))
            )
            val ARBEIDSSOKERPERIODE_10 = Arbeidssokerperiode(
                Periode(LocalDate.of(2021, 10, 17), null)
            )
        }
    }

    private class StubFormidlingsgruppeGateway : FormidlingsgruppeGateway {
        override fun finnArbeissokerperioder(foedselsnummer: Foedselsnummer, periode: Periode): Arbeidssokerperioder {
            val map: Map<Foedselsnummer, Arbeidssokerperioder> = mapOf(
                FOEDSELSNUMMER_3 to Arbeidssokerperioder(
                    listOf(
                        ARBEIDSSOKERPERIODE_2,
                        ARBEIDSSOKERPERIODE_4,
                        ARBEIDSSOKERPERIODE_3,
                        ARBEIDSSOKERPERIODE_0,
                        ARBEIDSSOKERPERIODE_1,
                        ARBEIDSSOKERPERIODE_5,
                        ARBEIDSSOKERPERIODE_6
                    )
                ),
                FOEDSELSNUMMER_4 to Arbeidssokerperioder(emptyList())
            )
            return map[foedselsnummer]!!
        }

        companion object {
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

    companion object {
        val FOEDSELSNUMMER_1: Foedselsnummer = Foedselsnummer("12345678911")
        val FOEDSELSNUMMER_2: Foedselsnummer = Foedselsnummer("11234567890")
        private val FOEDSELSNUMMER_3 = Foedselsnummer("22334455661")
        private val FOEDSELSNUMMER_4 = Foedselsnummer("99887766554")
        private val BRUKER_1 = Bruker.of(
            FOEDSELSNUMMER_3,
            AktorId("100002345678"),
            listOf(FOEDSELSNUMMER_2, FOEDSELSNUMMER_1)
        )
        private val BRUKER_3 = Bruker.of(
            FOEDSELSNUMMER_3,
            AktorId("100002345678"), emptyList()
        )
        private val BRUKER_2 = Bruker.of(
            FOEDSELSNUMMER_4,
            AktorId("100002339391"),
            emptyList()
        )
    }
}
