package no.nav.fo.veilarbregistrering.registrering.bruker

import io.mockk.*
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class InaktivBrukerServiceTest {
    private lateinit var inaktivBrukerService: InaktivBrukerService
    private val brukerRegistreringRepository: BrukerRegistreringRepository = mockk(relaxed = true)
    private val reaktiveringRepository: ReaktiveringRepository = mockk(relaxed = true)
    private val oppfolgingClient: OppfolgingClient = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        every { oppfolgingClient.reaktiverBruker(any()) } just Runs
        val oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient)
        inaktivBrukerService = InaktivBrukerService(
            BrukerTilstandService(
                oppfolgingGateway,
                brukerRegistreringRepository
            ),
            reaktiveringRepository,
            oppfolgingGateway
        )
    }

    @Test
    fun skalReaktivereInaktivBrukerUnder28Dager() {
        mockInaktivBrukerSomSkalReaktiveres()
        inaktivBrukerService.reaktiverBruker(BRUKER_INTERN)
        verify(exactly = 1) { reaktiveringRepository.lagreReaktiveringForBruker(any()) }
    }

    @Test
    fun reaktiveringAvBrukerOver28DagerSkalGiException() {
        mockInaktivBrukerSomSkalReaktiveres()
        mockOppfolgingMedRespons(
                OppfolgingStatusData()
                        .withUnderOppfolging(false)
                        .withKanReaktiveres(false)
        )
        Assertions.assertThrows(RuntimeException::class.java, { inaktivBrukerService.reaktiverBruker(BRUKER_INTERN) }, "Bruker kan ikke reaktiveres.")
        verify(exactly = 0) { reaktiveringRepository.lagreReaktiveringForBruker(any()) }
    }

    private fun mockInaktivBrukerSomSkalReaktiveres() =
            every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
                    OppfolgingStatusData()
                            .withUnderOppfolging(false)
                            .withKanReaktiveres(true)

    private fun mockOppfolgingMedRespons(oppfolgingStatusData: OppfolgingStatusData) =
            every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns oppfolgingStatusData

    companion object {
        private val FNR_OPPFYLLER_KRAV =
                FoedselsnummerTestdataBuilder.fodselsnummerOnDateMinusYears(LocalDate.now(), 40)
        private val BRUKER_INTERN = Bruker.of(FNR_OPPFYLLER_KRAV, AktorId.of("AKTØRID"))
    }
}