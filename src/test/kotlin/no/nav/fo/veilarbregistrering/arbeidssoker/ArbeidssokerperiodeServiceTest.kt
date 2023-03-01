package no.nav.fo.veilarbregistrering.arbeidssoker

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.junit.jupiter.api.Assertions.assertThrows
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

    @Test
    fun `gjør ingenting hvis ikke formidlingsgruppe er ISERV eller IARBS`() {
        every { repository.avsluttPeriode(any(), any()) } returns Unit
        service.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent = formidlingsgruppeEndret(LocalDateTime.now(), formidlingsgruppe = "ARBS"))
        verify(exactly = 0) { repository.avsluttPeriode(any(), any()) }
    }

    @Test
    fun `gjør ingenting hvis ikke bruker har en aktiv periode` () {
        every { repository.avsluttPeriode(any(), any()) } returns Unit
        every { repository.hentPerioder(any())} returns emptyList()

        service.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent = formidlingsgruppeEndret(LocalDateTime.now(), formidlingsgruppe = "ISERV"))

        verify(exactly = 0) { repository.avsluttPeriode(any(), any()) }
    }

    @Test
    fun `avslutte periode for bruker`() {
        every { repository.avsluttPeriode(any(), any()) } returns Unit
        every { repository.hentPerioder(Foedselsnummer("12345678910"))} returns listOf(ArbeidssokerperiodeDto(1, Foedselsnummer("12345678910"), LocalDateTime.now()))

        service.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent = formidlingsgruppeEndret(LocalDateTime.now(), formidlingsgruppe = "ISERV"))

        verify(exactly = 1) { repository.avsluttPeriode(Foedselsnummer("12345678910"), any()) }
    }

}
