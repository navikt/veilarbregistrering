package no.nav.fo.veilarbregistrering.arbeidssoker

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.UserService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class ArbeidssokerperiodeServiceTest {
    private lateinit var repository: ArbeidssokerperiodeRepository
    private lateinit var service: ArbeidssokerperiodeService
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp () {
        repository = mockk()
        userService = mockk()
        service = ArbeidssokerperiodeService(repository, userService)
    }

    @Test
    fun `kaster exception hvis bruker har en aktiv periode`() {
        val fnr = Foedselsnummer("42")
        every { repository.hentPerioder(any(), any()) } returns listOf(ArbeidssokerperiodeDto(1, fnr, LocalDateTime.now()))

        assertThrows(IllegalStateException::class.java) {
            service.startPeriode(Bruker(fnr, AktorId("1234"), emptyList()))
        }
    }

    @Test
    fun `starter periode for bruker`() {
        val bruker = Bruker(Foedselsnummer("42"), AktorId("1234"), emptyList())
        every { repository.hentPerioder(any(), any()) } returns emptyList()
        every { repository.startPeriode(any(), any()) } just Runs

        service.startPeriode(bruker)

        verify(exactly = 1) { repository.startPeriode(bruker.gjeldendeFoedselsnummer, any()) }
    }

    @Test
    fun `gjør ingenting hvis aktiv periode og ikke formidlingsgruppe er ISERV eller IARBS`() {
        every { userService.finnBrukerGjennomPdlForSystemkontekst(any()) } returns
                Bruker(Foedselsnummer("12345678910"), AktorId("1234"), emptyList())
        every { repository.hentPerioder(any(), any()) } returns
                listOf(ArbeidssokerperiodeDto(1, Foedselsnummer("12345678910"), LocalDateTime.now()))
        every { repository.avsluttPeriode(any<Int>(), any()) } just Runs
        service.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent = formidlingsgruppeEndret(LocalDateTime.now(), formidlingsgruppe = "ARBS"))
        verify(exactly = 0) { repository.avsluttPeriode(any<Int>(), any()) }
    }

    @Test
    fun `gjør ingenting hvis ikke bruker har en aktiv periode` () {
        every { userService.finnBrukerGjennomPdlForSystemkontekst(any()) } returns
                Bruker(Foedselsnummer("12345678910"), AktorId("1234"), emptyList())
        every { repository.avsluttPeriode(any<Int>(), any()) } just Runs
        every { repository.hentPerioder(any(), any()) } returns emptyList()

        service.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent = formidlingsgruppeEndret(LocalDateTime.now(), formidlingsgruppe = "ISERV"))

        verify(exactly = 0) { repository.avsluttPeriode(any<Int>(), any()) }
    }

    @Test
    fun `avslutte periode for bruker`() {
        every { userService.finnBrukerGjennomPdlForSystemkontekst(any()) } returns
                Bruker(Foedselsnummer("12345678910"), AktorId("1234"), emptyList())
        every { repository.avsluttPeriode(any<Int>(), any()) } just Runs
        every { repository.hentPerioder(Foedselsnummer("12345678910"), any()) } returns
                listOf(ArbeidssokerperiodeDto(1, Foedselsnummer("12345678910"), LocalDateTime.now()))

        service.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent = formidlingsgruppeEndret(LocalDateTime.now(), formidlingsgruppe = "ISERV"))

        verify(exactly = 1) { repository.avsluttPeriode(any<Int>(), any()) }
    }

    @Test
    fun `skal starte periode for bruker`() {
        every { userService.finnBrukerGjennomPdlForSystemkontekst(any()) } returns
                Bruker(Foedselsnummer("12345678910"), AktorId("1234"), emptyList())
        every { repository.startPeriode(any<Foedselsnummer>(), any()) } just Runs
        every { repository.hentPerioder(Foedselsnummer("12345678910"), any()) } returns emptyList()

        service.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent = formidlingsgruppeEndret(LocalDateTime.now(), formidlingsgruppe = "ARBS"))
        verify(exactly = 1) { repository.startPeriode(any<Foedselsnummer>(), any()) }
    }

    @Test
    fun `skal ikke starte periode for bruker hvis formidlingsgruppe ARBS og har en aktiv periode`() {
        every { userService.finnBrukerGjennomPdlForSystemkontekst(any()) } returns
                Bruker(Foedselsnummer("12345678910"), AktorId("1234"), emptyList())
        every { repository.startPeriode(any<Foedselsnummer>(), any()) } just Runs
        every { repository.hentPerioder(Foedselsnummer("12345678910"), any()) } returns
                listOf(ArbeidssokerperiodeDto(1, Foedselsnummer("12345678910"), LocalDateTime.now()))

        service.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent = formidlingsgruppeEndret(LocalDateTime.now(), formidlingsgruppe = "ARBS"))
        verify(exactly = 0) { repository.startPeriode(any<Foedselsnummer>(), any()) }
    }

}
