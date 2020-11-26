package no.nav.fo.veilarbregistrering.oppgave

import no.nav.apiapp.feil.Feil
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRouter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class OppgaveServiceTest {
    private var oppgaveService: OppgaveService? = null
    private var oppgaveGateway: OppgaveGateway? = null
    private var oppgaveRepository: OppgaveRepository? = null
    private var oppgaveRouter: OppgaveRouter? = null
    @BeforeEach
    fun setUp() {
        oppgaveGateway = Mockito.mock(OppgaveGateway::class.java)
        oppgaveRepository = Mockito.mock(OppgaveRepository::class.java)
        oppgaveRouter = Mockito.mock(OppgaveRouter::class.java)
        oppgaveService = CustomOppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                oppgaveRouter,
                KontaktBrukerHenvendelseProducer { aktorId: AktorId?, oppgaveType: OppgaveType? -> }
        )
    }

    @Test
    fun opprettOppgave_ang_opphold_skal_gi_beskrivelse_om_rutine() {
        Mockito.`when`(oppgaveRouter!!.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        Mockito.`when`(oppgaveGateway!!.opprett(ArgumentMatchers.any())).thenReturn(DummyOppgaveResponse())
        oppgaveService!!.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.OPPHOLDSTILLATELSE,
                LocalDate.of(2020, 4, 10))
        Mockito.verify(oppgaveGateway, Mockito.times(1))!!.opprett(oppgave)
    }

    @Test
    fun opprettOppgave_ang_dod_utvandret_skal_gi_beskrivelse_om_rutine() {
        Mockito.`when`(oppgaveRouter!!.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        Mockito.`when`(oppgaveGateway!!.opprett(ArgumentMatchers.any())).thenReturn(DummyOppgaveResponse())
        oppgaveService!!.opprettOppgave(BRUKER, OppgaveType.UTVANDRET)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.UTVANDRET,
                LocalDate.of(2020, 4, 10))
        Mockito.verify(oppgaveGateway, Mockito.times(1))!!.opprett(oppgave)
    }

    @Test
    fun skal_lagre_oppgave_ved_vellykket_opprettelse_av_oppgave() {
        Mockito.`when`(oppgaveRouter!!.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        Mockito.`when`(oppgaveGateway!!.opprett(ArgumentMatchers.any())).thenReturn(DummyOppgaveResponse())
        oppgaveService!!.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE)
        Mockito.verify(oppgaveRepository, Mockito.times(1))
                .opprettOppgave(BRUKER.aktorId, OppgaveType.OPPHOLDSTILLATELSE, 234L)
    }

    @Test
    fun skal_kaste_exception_dersom_det_finnes_nyere_oppholdsoppgave_fra_for() {
        val oppgaveSomBleOpprettetDagenFor = OppgaveImpl(23, BRUKER.aktorId, OppgaveType.OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 4, 9, 22, 0))
        val oppgaver = listOf(oppgaveSomBleOpprettetDagenFor)
        Mockito.`when`(oppgaveRepository!!.hentOppgaverFor(ArgumentMatchers.any())).thenReturn(oppgaver)
        Assertions.assertThrows(Feil::class.java) { oppgaveService!!.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE) }
        Mockito.verifyZeroInteractions(oppgaveGateway)
    }

    @Test
    fun skal_kaste_exception_dersom_det_finnes_nyere_utvandretoppgave_fra_for() {
        val oppgaveSomBleOpprettetDagenFor = OppgaveImpl(23, BRUKER.aktorId, OppgaveType.UTVANDRET, 23, LocalDateTime.of(2020, 4, 9, 22, 0))
        val oppgaver = listOf(oppgaveSomBleOpprettetDagenFor)
        Mockito.`when`(oppgaveRepository!!.hentOppgaverFor(ArgumentMatchers.any())).thenReturn(oppgaver)
        Assertions.assertThrows(Feil::class.java) { oppgaveService!!.opprettOppgave(BRUKER, OppgaveType.UTVANDRET) }
        Mockito.verifyZeroInteractions(oppgaveGateway)
    }

    @Test
    fun skal_ikke_kaste_exception_dersom_det_finnes_eldre_oppgave_fra_for() {
        Mockito.`when`(oppgaveRouter!!.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        val oppgaveSomBleOpprettetTreDagerFor = OppgaveImpl(23, BRUKER.aktorId, OppgaveType.OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 3, 10, 22, 0))
        val oppgaver = listOf(oppgaveSomBleOpprettetTreDagerFor)
        Mockito.`when`(oppgaveRepository!!.hentOppgaverFor(ArgumentMatchers.any())).thenReturn(oppgaver)
        Mockito.`when`(oppgaveGateway!!.opprett(ArgumentMatchers.any())).thenReturn(DummyOppgaveResponse())
        oppgaveService!!.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.OPPHOLDSTILLATELSE,
                LocalDate.of(2020, 4, 10))
        Mockito.verify(oppgaveGateway, Mockito.times(1))!!.opprett(oppgave)
    }

    @Test
    fun ingen_tidligere_oppgaver() {
        Mockito.`when`(oppgaveRouter!!.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        Mockito.`when`(oppgaveRepository!!.hentOppgaverFor(ArgumentMatchers.any())).thenReturn(emptyList())
        Mockito.`when`(oppgaveGateway!!.opprett(ArgumentMatchers.any())).thenReturn(DummyOppgaveResponse())
        oppgaveService!!.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.OPPHOLDSTILLATELSE,
                LocalDate.of(2020, 4, 10))
        Mockito.verify(oppgaveGateway, Mockito.times(1))!!.opprett(oppgave)
    }

    @Test
    fun skal_ikke_kaste_exception_dersom_det_finnes_oppgave_av_annen_type() {
        Mockito.`when`(oppgaveRouter!!.hentEnhetsnummerFor(BRUKER)).thenReturn(Optional.empty())
        val oppgaveSomBleOpprettetEnDagerFor = OppgaveImpl(23, BRUKER.aktorId, OppgaveType.OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 4, 9, 22, 0))
        val oppgaver = listOf(oppgaveSomBleOpprettetEnDagerFor)
        Mockito.`when`(oppgaveRepository!!.hentOppgaverFor(ArgumentMatchers.any())).thenReturn(oppgaver)
        Mockito.`when`(oppgaveGateway!!.opprett(ArgumentMatchers.any())).thenReturn(DummyOppgaveResponse())
        oppgaveService!!.opprettOppgave(BRUKER, OppgaveType.UTVANDRET)
        val oppgave = Oppgave.opprettOppgave(
                BRUKER.aktorId,
                null, OppgaveType.UTVANDRET,
                LocalDate.of(2020, 4, 10))
        Mockito.verify(oppgaveGateway, Mockito.times(1))!!.opprett(oppgave)
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