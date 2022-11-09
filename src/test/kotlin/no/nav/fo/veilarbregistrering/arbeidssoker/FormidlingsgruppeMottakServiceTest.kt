package no.nav.fo.veilarbregistrering.arbeidssoker

import io.mockk.*
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeMottakService
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent
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
            arbeidssokerperiodeAvsluttetService
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
