package no.nav.fo.veilarbregistrering.arbeidssoker

import io.mockk.*
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month

class ArbeidssokerServiceBehandleTest {
    private lateinit var arbeidssokerService: ArbeidssokerService
    private lateinit var arbeidssokerRepository: ArbeidssokerRepository
    private lateinit var arbeidssokerperiodeService: ArbeidssokerperiodeService

    @BeforeEach
    fun setup() {
        arbeidssokerRepository = mockk()
        arbeidssokerperiodeService = mockk()
        every { arbeidssokerperiodeService.behandleAvslutningAvArbeidssokerperiode(any(), any()) } just Runs
        arbeidssokerService = ArbeidssokerService(
            arbeidssokerRepository,
            arbeidssokerperiodeService,
            mockk(),
            mockk(),
            mockk()
        )
    }

    @Test
    fun `endringer fra 2010 skal persisteres`() {
        val formidlingsgruppeEvent = testEvent(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0, 0))
        every { arbeidssokerRepository.lagre(any()) } returns 1L
        every { arbeidssokerRepository.finnFormidlingsgrupper(any()) } returns Arbeidssokerperioder(emptyList())
        arbeidssokerService.behandle(formidlingsgruppeEvent)
        verify(exactly = 1) { arbeidssokerRepository.lagre(formidlingsgruppeEvent) }
    }

    @Test
    fun `endringer før 2010 skal ikke persisteres`() {
        val formidlingsgruppeEvent = testEvent(LocalDateTime.of(2009, Month.DECEMBER, 31, 23, 59, 59))
        every { arbeidssokerRepository.lagre(any()) } returns 1L
        arbeidssokerService.behandle(formidlingsgruppeEvent)
        verify(exactly = 0) { arbeidssokerRepository.lagre(formidlingsgruppeEvent) }
    }

    private fun testEvent(test: LocalDateTime): FormidlingsgruppeEvent {
        return FormidlingsgruppeEvent(
            Foedselsnummer("12345678910"),
            "012345",
            "AKTIV",
            Operation.UPDATE,
            Formidlingsgruppe("ISERV"),
            test,
            Formidlingsgruppe("ARBS"),
            test.minusDays(1)
        )
    }
}
