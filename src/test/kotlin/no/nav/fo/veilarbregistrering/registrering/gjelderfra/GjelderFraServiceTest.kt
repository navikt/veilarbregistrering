package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import io.mockk.*
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

class GjelderFraServiceTest {
    private lateinit var gjelderFraRepository: GjelderFraRepository
    private lateinit var brukerRegistreringRepository: BrukerRegistreringRepository
    private lateinit var gjelderFraService: GjelderFraService

    @BeforeEach
    fun setUp() {
        gjelderFraRepository = mockk()
        brukerRegistreringRepository = mockk()
        gjelderFraService = GjelderFraService(gjelderFraRepository, brukerRegistreringRepository)
    }

    @Test
    fun `returnerer dato for bruker`() {
        val gjelderFraDato = GjelderFraDato(
            id = 1,
            foedselsnummer = Foedselsnummer("11"),
            dato = LocalDate.of(2022, 6, 20),
            brukerRegistreringId = 42,
            opprettetDato = LocalDateTime.now()
        )

        every { gjelderFraRepository.hentDatoFor(bruker) } returns gjelderFraDato

        val resultat = gjelderFraService.hentDato(bruker)

        assertEquals(gjelderFraDato, resultat)
    }

    @Test
    fun `oppretter dato for bruker & registrering`() {
        val gjelderFraDato = GjelderFraDato(
            id = 1,
            foedselsnummer = Foedselsnummer("11"),
            dato = LocalDate.of(2022, 6, 20),
            brukerRegistreringId = 42,
            opprettetDato = LocalDateTime.now()
        )

        every { brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(any(), any())} returns listOf(OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering())

        var brukerSlot = slot<Bruker>()
        var registreringsIdSlot = slot<Long>()
        var datoSlot = slot<LocalDate>()

        every {
            gjelderFraRepository.opprettDatoFor(capture(brukerSlot), capture(registreringsIdSlot), capture(datoSlot))
        } just Runs

        gjelderFraService.opprettDato(bruker, LocalDate.of(2022, 6, 6))
        assertEquals(bruker, brukerSlot.captured)
        assertEquals(0, registreringsIdSlot.captured)
        assertEquals(LocalDate.of(2022, 6, 6), datoSlot.captured)
    }

    companion object {
        private val fnr = Foedselsnummer("11017724129")
        private val aktorId = AktorId("12311")
        private val bruker = Bruker(fnr, aktorId)
    }
}
