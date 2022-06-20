package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

class GjelderFraServiceTest {
    private lateinit var gjelderFraRepository: GjelderFraRepository
    private lateinit var gjelderFraService: GjelderFraService

    @BeforeEach
    fun setUp() {
        gjelderFraRepository = mockk()
        gjelderFraService = GjelderFraService(gjelderFraRepository)
    }

    @Test
    fun `returnerer dato for bruker`() {
        val gjelderFraDato = GjelderFraDato(bruker, OK_REGISTRERING, LocalDate.of(2022, 6, 6))
        every { gjelderFraRepository.hentDatoFor(bruker) } returns gjelderFraDato
        val resultat = gjelderFraService.hentDato(bruker)

        assertEquals(gjelderFraDato, resultat)
    }

    @Test
    fun `oppretter dato for bruker & registrering`() {
        val gjelderFraDato = GjelderFraDato(bruker, OK_REGISTRERING, LocalDate.of(2022, 6, 6))
        every {
            gjelderFraService.opprettDato(bruker, OK_REGISTRERING, LocalDate.of(2022, 6, 6))
        } returns gjelderFraDato

        val resultat = gjelderFraService.opprettDato(bruker, OK_REGISTRERING, LocalDate.of(2022, 6, 6))

        assertEquals(gjelderFraDato, resultat)
    }

    companion object {
        private val fnr = Foedselsnummer("11017724129")
        private val aktorId = AktorId("12311")
        private val bruker = Bruker(fnr, aktorId)

        private val igaar = LocalDateTime.now().minusDays(1)

        private val profilering = ProfileringTestdataBuilder.lagProfilering()
        private val OK_REGISTRERING = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
            opprettetDato = igaar,
            profilering = profilering
        )
    }
}
