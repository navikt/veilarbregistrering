package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder.testEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month

class FormidlingsgruppeMottakServiceTest {
    private lateinit var formidlingsgruppeMottakService: FormidlingsgruppeMottakService
    private lateinit var formidlingsgruppeRepository: FormidlingsgruppeRepository

    @BeforeEach
    fun setup() {
        formidlingsgruppeRepository = mockk()
        formidlingsgruppeMottakService = FormidlingsgruppeMottakService(
            formidlingsgruppeRepository,
            mockk(relaxed = true),
            mockk(relaxed = true)
        )
    }

    @Test
    fun `endringer fra 2010 skal persisteres`() {
        val formidlingsgruppeEvent = testEvent(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0, 0), Operation.UPDATE)
        every { formidlingsgruppeRepository.lagre(any()) } returns 1L
        every { formidlingsgruppeRepository.finnFormidlingsgruppeEndretEventFor(any()) } returns emptyList()

        formidlingsgruppeMottakService.behandle(formidlingsgruppeEvent)

        verify(exactly = 1) { formidlingsgruppeRepository.lagre(formidlingsgruppeEvent) }
    }

    @Test
    fun `insert skal lagres men ikke behandles videre`() {
        val formidlingsgruppeEvent = testEvent(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0, 0), Operation.INSERT)
        every { formidlingsgruppeRepository.lagre(any()) } returns 1L
        every { formidlingsgruppeRepository.finnFormidlingsgruppeEndretEventFor(any()) } returns emptyList()

        formidlingsgruppeMottakService.behandle(formidlingsgruppeEvent)

        verify(exactly = 1) { formidlingsgruppeRepository.lagre(formidlingsgruppeEvent) }
    }
}
