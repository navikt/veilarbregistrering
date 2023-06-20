package no.nav.fo.veilarbregistrering.arbeidssoker

import io.mockk.*
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerperiodeProducer
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class ArbeidssokerperiodeServiceTest {
    private lateinit var repository: ArbeidssokerperiodeRepository
    private lateinit var service: ArbeidssokerperiodeService
    private lateinit var userService: UserService
    private lateinit var arbeidssokerperiodeProducer: ArbeidssokerperiodeProducer

    @BeforeEach
    fun setUp() {
        repository = mockk()
        userService = mockk()
        arbeidssokerperiodeProducer = mockk()
        service = ArbeidssokerperiodeService(repository, userService, arbeidssokerperiodeProducer)
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
        every { arbeidssokerperiodeProducer.publiserArbeidssokerperiode(any()) } returns true

        service.startPeriode(bruker)

        verify(exactly = 1) { repository.startPeriode(bruker.gjeldendeFoedselsnummer, any()) }
    }

    @Test
    fun `gjør ingenting hvis ikke formidlingsgruppe er ISERV eller IARBS`() {
        every { repository.avsluttPeriode(any<Int>(), any()) } just Runs
        every { arbeidssokerperiodeProducer.publiserArbeidssokerperiode(any()) } returns true

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
        every { arbeidssokerperiodeProducer.publiserArbeidssokerperiode(any()) } returns true

        service.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent = formidlingsgruppeEndret(LocalDateTime.now(), formidlingsgruppe = "ISERV"))

        verify(exactly = 1) { repository.avsluttPeriode(any<Int>(), any()) }
    }

}
