package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import io.mockk.*
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerperiodeAvsluttetService
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month

class FormidlingsgruppeMottakServiceTest {
    private lateinit var formidlingsgruppeMottakService: FormidlingsgruppeMottakService
    private lateinit var formidlingsgruppeRepository: FormidlingsgruppeRepository
    private lateinit var arbeidssokerperiodeAvsluttetService: ArbeidssokerperiodeAvsluttetService

    @BeforeEach
    fun setup() {
        formidlingsgruppeRepository = mockk()
        arbeidssokerperiodeAvsluttetService = mockk()
        every { arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(any(), any()) } just Runs
        formidlingsgruppeMottakService = FormidlingsgruppeMottakService(
            formidlingsgruppeRepository,
            arbeidssokerperiodeAvsluttetService,
            mockk(relaxed = true),
            mockk(relaxed = true)
        )
    }

    @Test
    fun `endringer fra 2010 skal persisteres`() {
        val formidlingsgruppeEvent = testEvent(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0, 0))
        every { formidlingsgruppeRepository.lagre(any()) } returns 1L
        every { formidlingsgruppeRepository.finnFormidlingsgrupperOgMapTilArbeidssokerperioder(any()) } returns Arbeidssokerperioder(emptyList())
        formidlingsgruppeMottakService.behandle(formidlingsgruppeEvent)
        verify(exactly = 1) { formidlingsgruppeRepository.lagre(formidlingsgruppeEvent) }
    }

    private fun testEvent(test: LocalDateTime): FormidlingsgruppeEndretEvent {
        return FormidlingsgruppeEndretEvent(
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
