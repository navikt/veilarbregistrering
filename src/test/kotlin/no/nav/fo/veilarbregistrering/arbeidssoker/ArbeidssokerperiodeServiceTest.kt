package no.nav.fo.veilarbregistrering.arbeidssoker

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class ArbeidssokerperiodeServiceTest {
    private lateinit var repository: ArbeidssokerperiodeRepository
    private lateinit var service: ArbeidssokerperiodeService

    @BeforeEach
    fun setUp () {
        repository = mockk()
        service = ArbeidssokerperiodeService(repository)
    }

    @Test
    fun `kaster exception hvis bruker har en aktiv periode`() {
        val fnr = Foedselsnummer("42")
        every { repository.hentPerioder(any()) } returns listOf(ArbeidssokerperiodeDto(1, fnr, LocalDateTime.now()))

        assertThrows(IllegalStateException::class.java) {
            service.startPeriode(fnr)
        }
    }

    @Test
    fun `starter periode for bruker`() {
        val fnr = Foedselsnummer("42")
        every { repository.hentPerioder(any()) } returns emptyList()
        every { repository.startPeriode(any(), any()) } returns Unit

        service.startPeriode(fnr)

        verify(exactly = 1) { repository.startPeriode(foedselsnummer = fnr, any())}
    }

}
