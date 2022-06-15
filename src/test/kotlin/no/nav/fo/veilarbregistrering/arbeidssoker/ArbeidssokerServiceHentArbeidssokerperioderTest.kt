package no.nav.fo.veilarbregistrering.arbeidssoker


import io.mockk.every
import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidssokerServiceHentArbeidssokerperioderTest {
    private lateinit var arbeidssokerService: ArbeidssokerService
    private val unleashService = mockk<UnleashClient>()
    private val metricsService = mockk<MetricsService>(relaxed = true)

    @BeforeEach
    fun setup() {
        arbeidssokerService = ArbeidssokerService(
            StubArbeidssokerRepository(),
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
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_1,
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_2,
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_3,
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_4
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
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_0,
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_1,
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_2,
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_3,
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_4
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
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_3,
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_4,
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_5,
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_6,
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_7
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
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_1,
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_2,
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_3,
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_4,
            StubFormidlingsgruppeGateway.FORMIDLINGSGRUPPEPERIODE_5
        )
    }

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
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_9,
            StubArbeidssokerRepository.FORMIDLINGSGRUPPEPERIODE_10,
        )
    }

    private class StubArbeidssokerRepository : ArbeidssokerRepository {
        override fun lagre(command: EndretFormidlingsgruppeCommand): Long {
            return 0
        }

        override fun finnFormidlingsgrupper(foedselsnummerList: List<Foedselsnummer>): Arbeidssokerperioder {
            return mapOf(
                FOEDSELSNUMMER_3 to Arbeidssokerperioder(
                    listOf(
                        FORMIDLINGSGRUPPEPERIODE_3,
                        FORMIDLINGSGRUPPEPERIODE_1,
                        FORMIDLINGSGRUPPEPERIODE_4,
                        FORMIDLINGSGRUPPEPERIODE_2,
                        FORMIDLINGSGRUPPEPERIODE_6,
                        FORMIDLINGSGRUPPEPERIODE_5,
                        FORMIDLINGSGRUPPEPERIODE_7,
                        FORMIDLINGSGRUPPEPERIODE_8,
                    )
                ),
                FOEDSELSNUMMER_4 to Arbeidssokerperioder(
                    listOf(
                        FORMIDLINGSGRUPPEPERIODE_9,
                        FORMIDLINGSGRUPPEPERIODE_10,
                    ),
                )
            ).entries.first { (fnr, _) -> fnr in foedselsnummerList }.value
        }

        companion object {
            val FORMIDLINGSGRUPPEPERIODE_1 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
            )
            val FORMIDLINGSGRUPPEPERIODE_2 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 29))
            )
            val FORMIDLINGSGRUPPEPERIODE_3 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
            )
            val FORMIDLINGSGRUPPEPERIODE_4 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 2))
            )
            val FORMIDLINGSGRUPPEPERIODE_5 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 5, 3), LocalDate.of(2020, 5, 9))
            )
            val FORMIDLINGSGRUPPEPERIODE_6 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 5, 10), LocalDate.of(2020, 5, 29))
            )
            val FORMIDLINGSGRUPPEPERIODE_7 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 5, 30), LocalDate.of(2020, 6, 30))
            )
            val FORMIDLINGSGRUPPEPERIODE_8 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 7, 1), null)
            )
            val FORMIDLINGSGRUPPEPERIODE_9 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2021, 1, 15), LocalDate.of(2021, 10, 5))
            )
            val FORMIDLINGSGRUPPEPERIODE_10 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2021, 10, 17), null)
            )
        }
    }

    private class StubFormidlingsgruppeGateway : FormidlingsgruppeGateway {
        override fun finnArbeissokerperioder(foedselsnummer: Foedselsnummer, periode: Periode): Arbeidssokerperioder {
            val map: Map<Foedselsnummer, Arbeidssokerperioder> = mapOf(
                FOEDSELSNUMMER_3 to Arbeidssokerperioder(
                    listOf(
                        FORMIDLINGSGRUPPEPERIODE_2,
                        FORMIDLINGSGRUPPEPERIODE_4,
                        FORMIDLINGSGRUPPEPERIODE_3,
                        FORMIDLINGSGRUPPEPERIODE_0,
                        FORMIDLINGSGRUPPEPERIODE_1,
                        FORMIDLINGSGRUPPEPERIODE_5,
                        FORMIDLINGSGRUPPEPERIODE_6
                    )
                ),
                FOEDSELSNUMMER_4 to Arbeidssokerperioder(emptyList())
            )
            return map[foedselsnummer]!!
        }

        companion object {
            val FORMIDLINGSGRUPPEPERIODE_0 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 31))
            )
            val FORMIDLINGSGRUPPEPERIODE_1 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
            )
            val FORMIDLINGSGRUPPEPERIODE_2 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 29))
            )
            val FORMIDLINGSGRUPPEPERIODE_3 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
            )
            val FORMIDLINGSGRUPPEPERIODE_4 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 2))
            )
            val FORMIDLINGSGRUPPEPERIODE_5 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
                Periode(LocalDate.of(2020, 5, 3), LocalDate.of(2020, 5, 9))
            )
            val FORMIDLINGSGRUPPEPERIODE_6 = Formidlingsgruppeperiode(
                Formidlingsgruppe("ARBS"),
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
