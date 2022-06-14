package no.nav.fo.veilarbregistrering.registrering.reaktivering

import io.mockk.*
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.adapter.ErUnderOppfolgingDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.KanReaktiveresDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.VeilarbarenaClient
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerTilstandService
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringBrukerService
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ReaktiveringBrukerServiceTest {
    private lateinit var reaktiveringBrukerService: ReaktiveringBrukerService
    private val brukerRegistreringRepository: BrukerRegistreringRepository = mockk(relaxed = true)
    private val reaktiveringRepository: ReaktiveringRepository = mockk(relaxed = true)
    private val oppfolgingClient: OppfolgingClient = mockk(relaxed = true)
    private val veilarbarenaClient: VeilarbarenaClient = mockk(relaxed = true)
    private val metricsService: MetricsService = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        every { oppfolgingClient.reaktiverBruker(any()) } just Runs
        val oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient, veilarbarenaClient)
        reaktiveringBrukerService = ReaktiveringBrukerService(
                BrukerTilstandService(
                    oppfolgingGateway,
                    brukerRegistreringRepository
                ),
                reaktiveringRepository,
                oppfolgingGateway,
                metricsService
        )
    }

    @Test
    fun skalReaktivereInaktivBrukerUnder28Dager() {
        mockInaktivBrukerSomSkalReaktiveres()
        reaktiveringBrukerService.reaktiverBruker(BRUKER_INTERN, false)
        verify(exactly = 1) { reaktiveringRepository.lagreReaktiveringForBruker(any()) }

    }

    @Test
    fun reaktiveringAvBrukerOver28DagerSkalGiException() {
        mockBrukerSomIkkeSkalReaktiveres()
        Assertions.assertThrows(RuntimeException::class.java, { reaktiveringBrukerService.reaktiverBruker(BRUKER_INTERN, false) }, "Bruker kan ikke reaktiveres.")
        verify(exactly = 0) { reaktiveringRepository.lagreReaktiveringForBruker(any()) }
    }

    private fun mockInaktivBrukerSomSkalReaktiveres() {
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(false)
        every { veilarbarenaClient.kanReaktiveres(any()) } returns KanReaktiveresDto(true)
    }

    private fun mockBrukerSomIkkeSkalReaktiveres() {
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(false)
        every { veilarbarenaClient.kanReaktiveres(any()) } returns KanReaktiveresDto(false)
    }

    companion object {
        private val FNR_OPPFYLLER_KRAV =
                FoedselsnummerTestdataBuilder.fodselsnummerOnDateMinusYears(LocalDate.now(), 40)
        private val BRUKER_INTERN = Bruker(FNR_OPPFYLLER_KRAV, AktorId("AKTØRID"))
    }
}