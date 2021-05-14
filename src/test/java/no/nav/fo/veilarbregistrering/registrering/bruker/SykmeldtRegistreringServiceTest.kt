package no.nav.fo.veilarbregistrering.registrering.bruker

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SykmeldtRegistreringServiceTest {
    private lateinit var sykmeldtRegistreringService: SykmeldtRegistreringService

    private val brukerRegistreringRepository: BrukerRegistreringRepository = mockk(relaxed = true)
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository = mockk(relaxed = true)
    private val manuellRegistreringRepository: ManuellRegistreringRepository = mockk(relaxed = true)
    private val oppfolgingClient: OppfolgingClient = mockk(relaxed = true)
    private val autorisasjonService: AutorisasjonService = mockk()
    private val influxMetricsService: InfluxMetricsService = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        val oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient)
        brukerRegistreringRepository

        sykmeldtRegistreringService = SykmeldtRegistreringService(
            BrukerTilstandService(
                oppfolgingGateway,
                brukerRegistreringRepository
            ),
            oppfolgingGateway,
            sykmeldtRegistreringRepository,
            manuellRegistreringRepository,
            influxMetricsService
        )
    }

    @Test
    fun skalIkkeRegistrereSykmeldteMedTomBesvarelse() {
        mockSykmeldtMedArbeidsgiver()
        val sykmeldtRegistrering = SykmeldtRegistrering().setBesvarelse(null)
        Assertions.assertThrows(RuntimeException::class.java) {
            sykmeldtRegistreringService.registrerSykmeldt(
                sykmeldtRegistrering,
                BRUKER_INTERN,
                null
            )
        }
    }

    @Test
    fun skalIkkeRegistrereSykmeldtSomIkkeOppfyllerKrav() {
        mockSykmeldtUtenArbeidsgiver()
        val sykmeldtRegistrering = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering()
        Assertions.assertThrows(RuntimeException::class.java, {
            sykmeldtRegistreringService.registrerSykmeldt(
                sykmeldtRegistrering,
                BRUKER_INTERN,
                null
            )
        }, "Besvarelse for sykmeldt ugyldig.")
    }

    @Test
    fun gitt_at_veileder_ikke_er_angitt_skal_registrering_lagres_uten_navident() {
        mockSykmeldtMedArbeidsgiver()
        every {
            sykmeldtRegistreringRepository.lagreSykmeldtBruker(any(), any())
        } returns 5L
        every { autorisasjonService.erInternBruker() } returns false
        val sykmeldtRegistrering = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering()
        val id = sykmeldtRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER_INTERN, null)
        assertThat(id).isEqualTo(5)
        verify { manuellRegistreringRepository wasNot Called }
    }

    @Test
    fun gitt_at_veileder_er_angitt_skal_registrering_lagres_med_navident() {
        mockSykmeldtMedArbeidsgiver()
        every { autorisasjonService.erInternBruker() } returns true
        every {
            sykmeldtRegistreringRepository.lagreSykmeldtBruker(
                any(),
                any()
            )
        } returns 5L
        val sykmeldtRegistrering = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering()
        val id = sykmeldtRegistreringService.registrerSykmeldt(
            sykmeldtRegistrering,
            BRUKER_INTERN,
            NavVeileder("Z123456", "Ustekveikja")
        )
        assertThat(id).isEqualTo(5)
        verify(exactly = 1) { manuellRegistreringRepository.lagreManuellRegistrering(any()) }
    }

    private fun mockSykmeldtUtenArbeidsgiver() =
            every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
                    OppfolgingStatusData()
                            .withErSykmeldtMedArbeidsgiver(false)
                            .withKanReaktiveres(false)

    private fun mockSykmeldtMedArbeidsgiver() =
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
            OppfolgingStatusData()
                .withErSykmeldtMedArbeidsgiver(true)
                .withKanReaktiveres(false)

    companion object {
        private val FNR_OPPFYLLER_KRAV =
            FoedselsnummerTestdataBuilder.fodselsnummerOnDateMinusYears(LocalDate.now(), 40)
        private val BRUKER_INTERN = Bruker.of(FNR_OPPFYLLER_KRAV, AktorId.of("AKTØRID"))
    }
}