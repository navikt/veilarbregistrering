package no.nav.fo.veilarbregistrering.arbeidssoker


import io.mockk.every
import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidssokerServiceHentArbeidssokerperioderTest {
    private lateinit var arbeidssokerService: ArbeidssokerService
    private val unleashService = mockk<UnleashClient>()
    private val prometheusMetricsService = mockk<PrometheusMetricsService>(relaxed = true)

    @BeforeEach
    fun setup() {
        arbeidssokerService = ArbeidssokerService(
            StubArbeidssokerRepository(),
            StubFormidlingsgruppeGateway(),
            unleashService,
            prometheusMetricsService,
        )
    }

    @Test
    fun `hentArbeidssokerperioder skal returnere perioder sortert etter fradato`() {
        every { unleashService.isEnabled(ArbeidssokerService.VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE) } returns true

        val forespurtPeriode = Periode.of(
            LocalDate.of(2020, 1, 2),
            LocalDate.of(2020, 5, 1)
        )
        val arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(BRUKER_3, forespurtPeriode)
        assertThat(arbeidssokerperiodes.eldsteFoerst()).containsExactly(
            StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_1,
            StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_2,
            StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_3,
            StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_4
        )
    }

    @Test
    fun `hentArbeidssokerperioder skal hente fra ords`() {
        val forespurtPeriode = Periode.of(
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
        val forespurtPeriode = Periode.of(
            LocalDate.of(2019, 5, 1),
            LocalDate.of(2019, 11, 30)
        )
        val arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(BRUKER_3, forespurtPeriode)
        assertThat(arbeidssokerperiodes.asList()).isEmpty()
    }

    @Test
    fun `hentArbeidssokerperioder ingen treff på bruker skal returnere tom liste`() {
        val forespurtPeriode = Periode.of(
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
            AktorId.of("100002345678"),
            listOf(FOEDSELSNUMMER_2, FOEDSELSNUMMER_1)
        )
        val forespurtPeriode = Periode.of(
            LocalDate.of(2020, 3, 20),
            LocalDate.of(2020, 6, 10)
        )
        val arbeidssokerperioder = arbeidssokerService.hentArbeidssokerperioder(bruker, forespurtPeriode)
        assertThat(arbeidssokerperioder.eldsteFoerst()).containsExactly(
            StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_3,
            StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_4,
            StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_5,
            StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_6,
            StubArbeidssokerRepository.ARBEIDSSOKERPERIODE_7
        )
    }

    @Test
    fun `hentArbeidssokerperioder skal returnere alle perioder for person innenfor forespurt periode ords`() {
        every {
            unleashService.isEnabled(ArbeidssokerService.VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)
        } returns true

        val forespurtPeriode = Periode.of(
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

    private class StubArbeidssokerRepository : ArbeidssokerRepository {
        override fun lagre(command: EndretFormidlingsgruppeCommand): Long {
            return 0
        }

        override fun finnFormidlingsgrupper(foedselsnummerList: List<Foedselsnummer>): Arbeidssokerperioder {
            return Arbeidssokerperioder(
                listOf(
                    ARBEIDSSOKERPERIODE_3,
                    ARBEIDSSOKERPERIODE_1,
                    ARBEIDSSOKERPERIODE_4,
                    ARBEIDSSOKERPERIODE_2,
                    ARBEIDSSOKERPERIODE_6,
                    ARBEIDSSOKERPERIODE_5,
                    ARBEIDSSOKERPERIODE_7,
                    ARBEIDSSOKERPERIODE_8
                )
            )
        }

        companion object {
            val ARBEIDSSOKERPERIODE_1 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
            )
            val ARBEIDSSOKERPERIODE_2 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 29))
            )
            val ARBEIDSSOKERPERIODE_3 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
            )
            val ARBEIDSSOKERPERIODE_4 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 2))
            )
            val ARBEIDSSOKERPERIODE_5 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 3), LocalDate.of(2020, 5, 9))
            )
            val ARBEIDSSOKERPERIODE_6 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 10), LocalDate.of(2020, 5, 29))
            )
            val ARBEIDSSOKERPERIODE_7 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 30), LocalDate.of(2020, 6, 30))
            )
            val ARBEIDSSOKERPERIODE_8 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 7, 1), null)
            )
        }
    }

    private class StubFormidlingsgruppeGateway : FormidlingsgruppeGateway {
        override fun finnArbeissokerperioder(foedselsnummer: Foedselsnummer, periode: Periode): Arbeidssokerperioder {
            val map: MutableMap<Foedselsnummer, Arbeidssokerperioder> = HashMap()
            map[FOEDSELSNUMMER_3] = Arbeidssokerperioder(
                listOf(
                    ARBEIDSSOKERPERIODE_2,
                    ARBEIDSSOKERPERIODE_4,
                    ARBEIDSSOKERPERIODE_3,
                    ARBEIDSSOKERPERIODE_0,
                    ARBEIDSSOKERPERIODE_1,
                    ARBEIDSSOKERPERIODE_5,
                    ARBEIDSSOKERPERIODE_6
                )
            )
            return map[foedselsnummer]!!
        }

        companion object {
            val ARBEIDSSOKERPERIODE_0 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 31))
            )
            val ARBEIDSSOKERPERIODE_1 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
            )
            val ARBEIDSSOKERPERIODE_2 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 29))
            )
            val ARBEIDSSOKERPERIODE_3 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
            )
            val ARBEIDSSOKERPERIODE_4 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 2))
            )
            val ARBEIDSSOKERPERIODE_5 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 3), LocalDate.of(2020, 5, 9))
            )
            val ARBEIDSSOKERPERIODE_6 = Arbeidssokerperiode(
                Formidlingsgruppe.of("ARBS"),
                Periode.of(LocalDate.of(2020, 5, 10), null)
            )
        }
    }

    companion object {
        val FOEDSELSNUMMER_1: Foedselsnummer = Foedselsnummer.of("12345678911")
        val FOEDSELSNUMMER_2: Foedselsnummer = Foedselsnummer.of("11234567890")
        private val FOEDSELSNUMMER_3 = Foedselsnummer.of("22334455661")
        private val FOEDSELSNUMMER_4 = Foedselsnummer.of("99887766554")
        private val BRUKER_1 = Bruker.of(
            FOEDSELSNUMMER_3,
            AktorId.of("100002345678"),
            listOf(FOEDSELSNUMMER_2, FOEDSELSNUMMER_1)
        )
        private val BRUKER_3 = Bruker.of(
            FOEDSELSNUMMER_3,
            AktorId.of("100002345678"), emptyList()
        )
    }
}
