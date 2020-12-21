package no.nav.fo.veilarbregistrering.oppgave

import com.nhaarman.mockitokotlin2.*
import no.nav.apiapp.feil.Feil
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class OppgaveServiceTest {
    private lateinit var oppgaveService: OppgaveService
    private lateinit var oppgaveGateway: OppgaveGateway
    private lateinit var oppgaveRepository: OppgaveRepository
    private lateinit var oppgaveRouter: OppgaveRouter
    
    @BeforeEach
    fun setUp() {
        oppgaveGateway = mock()
        oppgaveRepository = mock()
        oppgaveRouter = mock()
        oppgaveService = CustomOppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                oppgaveRouter,
                KontaktBrukerHenvendelseProducer { aktorId: AktorId?, oppgaveType: OppgaveType? -> }
        )
    }

    @Test
    fun `opprettOppgave ang opphold skal gi beskrivelse om rutine`() {
        whenever(oppgaveRouter.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        whenever(oppgaveGateway.opprett(any())).thenReturn(DummyOppgaveResponse())
        oppgaveService.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.OPPHOLDSTILLATELSE,
                LocalDate.of(2020, 4, 10))
        verify(oppgaveGateway, times(1)).opprett(oppgave)
    }

    @Test
    fun `opprettOppgave ang dod utvandret skal gi beskrivelse om rutine`() {
        whenever(oppgaveRouter.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        whenever(oppgaveGateway.opprett(any())).thenReturn(DummyOppgaveResponse())
        oppgaveService.opprettOppgave(BRUKER, OppgaveType.UTVANDRET)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.UTVANDRET,
                LocalDate.of(2020, 4, 10))
        verify(oppgaveGateway, times(1)).opprett(oppgave)
    }

    @Test
    fun `skal lagre oppgave ved vellykket opprettelse av oppgave`() {
        whenever(oppgaveRouter.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        whenever(oppgaveGateway.opprett(any())).thenReturn(DummyOppgaveResponse())
        oppgaveService.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE)
        verify(oppgaveRepository, times(1)).opprettOppgave(BRUKER.aktorId, OppgaveType.OPPHOLDSTILLATELSE, 234L)
    }

    @Test
    fun `skal kaste exception dersom det finnes nyere oppholdsoppgave fra for`() {
        val oppgaveSomBleOpprettetDagenFor = OppgaveImpl(23, BRUKER.aktorId, OppgaveType.OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 4, 9, 22, 0))
        val oppgaver = listOf(oppgaveSomBleOpprettetDagenFor)
        whenever(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver)
        Assertions.assertThrows(Feil::class.java) { oppgaveService.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE) }
        verifyZeroInteractions(oppgaveGateway)
    }

    @Test
    fun `skal kaste exception dersom det finnes nyere utvandretoppgave fra for`() {
        val oppgaveSomBleOpprettetDagenFor = OppgaveImpl(23, BRUKER.aktorId, OppgaveType.UTVANDRET, 23, LocalDateTime.of(2020, 4, 9, 22, 0))
        val oppgaver = listOf(oppgaveSomBleOpprettetDagenFor)
        whenever(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver)
        Assertions.assertThrows(Feil::class.java) { oppgaveService.opprettOppgave(BRUKER, OppgaveType.UTVANDRET) }
        verifyZeroInteractions(oppgaveGateway)
    }

    @Test
    fun `skal ikke kaste exception dersom det finnes eldre oppgave fra for`() {
        whenever(oppgaveRouter.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        val oppgaveSomBleOpprettetTreDagerFor = OppgaveImpl(23, BRUKER.aktorId, OppgaveType.OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 3, 10, 22, 0))
        val oppgaver = listOf(oppgaveSomBleOpprettetTreDagerFor)
        whenever(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver)
        whenever(oppgaveGateway.opprett(any())).thenReturn(DummyOppgaveResponse())
        oppgaveService.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.OPPHOLDSTILLATELSE,
                LocalDate.of(2020, 4, 10))
        verify(oppgaveGateway, times(1)).opprett(oppgave)
    }

    @Test
    fun `ingen tidligere oppgaver`() {
        whenever(oppgaveRouter.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        whenever(oppgaveRepository.hentOppgaverFor(any())).thenReturn(emptyList())
        whenever(oppgaveGateway.opprett(any())).thenReturn(DummyOppgaveResponse())
        oppgaveService.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.OPPHOLDSTILLATELSE,
                LocalDate.of(2020, 4, 10))
        verify(oppgaveGateway, times(1)).opprett(oppgave)
    }

    @Test
    fun `skal ikke kaste exception dersom det finnes oppgave av annen type`() {
        whenever(oppgaveRouter.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        val oppgaveSomBleOpprettetEnDagerFor = OppgaveImpl(23, BRUKER.aktorId, OppgaveType.OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 4, 9, 22, 0))
        val oppgaver = listOf(oppgaveSomBleOpprettetEnDagerFor)
        whenever(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver)
        whenever(oppgaveGateway.opprett(any())).thenReturn(DummyOppgaveResponse())
        oppgaveService.opprettOppgave(BRUKER, OppgaveType.UTVANDRET)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.UTVANDRET,
                LocalDate.of(2020, 4, 10))
        verify(oppgaveGateway, times(1)).opprett(oppgave)
    }

    private class DummyOppgaveResponse : OppgaveResponse {
        override fun getId(): Long {
            return 234L
        }

        override fun getTildeltEnhetsnr(): String {
            return "0393"
        }
    }

    private class CustomOppgaveService(
            oppgaveGateway: OppgaveGateway?,
            oppgaveRepository: OppgaveRepository?,
            oppgaveRouter: OppgaveRouter?,
            kontaktBrukerHenvendelseProducer: KontaktBrukerHenvendelseProducer?) : OppgaveService(oppgaveGateway, oppgaveRepository, oppgaveRouter, kontaktBrukerHenvendelseProducer) {
        override fun idag(): LocalDate {
            return LocalDate.of(2020, 4, 10)
        }
    }

    companion object {
        private val BRUKER = Bruker.of(
                Foedselsnummer.of("12345678911"),
                AktorId.of("2134"))
    }
}