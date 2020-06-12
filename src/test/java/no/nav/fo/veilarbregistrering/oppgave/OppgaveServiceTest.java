package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.apiapp.feil.Feil;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static no.nav.fo.veilarbregistrering.oppgave.OppgaveType.OPPHOLDSTILLATELSE;
import static no.nav.fo.veilarbregistrering.oppgave.OppgaveType.UTVANDRET;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class OppgaveServiceTest {

    private static final Bruker BRUKER = Bruker.of(
            Foedselsnummer.of("12345678911"),
            AktorId.of("2134"));

    private OppgaveService oppgaveService;
    private OppgaveGateway oppgaveGateway;
    private OppgaveRepository oppgaveRepository;
    private OppgaveRouter oppgaveRouter;
    private BrukerRegistreringRepository brukerRegistreringRepository;

    @BeforeEach
    public void setUp() {
        oppgaveGateway = mock(OppgaveGateway.class);
        oppgaveRepository = mock(OppgaveRepository.class);
        oppgaveRouter = mock(OppgaveRouter.class);
        brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);

        oppgaveService = new CustomOppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                oppgaveRouter,
                (aktorId, oppgaveType) -> { },
                brukerRegistreringRepository);
    }

    @Test
    public void opprettOppgave_ang_opphold_skal_gi_beskrivelse_om_rutine() {
        when(oppgaveRouter.hentEnhetsnummerFor(BRUKER, OPPHOLDSTILLATELSE)).thenReturn(Optional.empty());
        when(oppgaveGateway.opprett(any())).thenReturn(new DummyOppgaveResponse());

        oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE);

        Oppgave oppgave = Oppgave.opprettOppgave(
                BRUKER.getAktorId(),
                null,
                OPPHOLDSTILLATELSE,
                LocalDate.of(2020, 4, 10));

        verify(oppgaveGateway, times(1)).opprett(oppgave);
    }

    @Test
    public void opprettOppgave_ang_dod_utvandret_skal_gi_beskrivelse_om_rutine() {
        when(oppgaveRouter.hentEnhetsnummerFor(BRUKER, OPPHOLDSTILLATELSE)).thenReturn(Optional.empty());
        when(oppgaveGateway.opprett(any())).thenReturn(new DummyOppgaveResponse());

        oppgaveService.opprettOppgave(BRUKER, UTVANDRET);

        Oppgave oppgave = Oppgave.opprettOppgave(
                BRUKER.getAktorId(),
                null,
                UTVANDRET,
                LocalDate.of(2020, 4, 10));

        verify(oppgaveGateway, times(1)).opprett(oppgave);
    }

    @Test
    public void skal_lagre_oppgave_ved_vellykket_opprettelse_av_oppgave() {
        when(oppgaveRouter.hentEnhetsnummerFor(BRUKER, OPPHOLDSTILLATELSE)).thenReturn(Optional.empty());
        when(oppgaveGateway.opprett(any())).thenReturn(new DummyOppgaveResponse());
        oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE);

        verify(oppgaveRepository, times(1))
                .opprettOppgave(BRUKER.getAktorId(), OPPHOLDSTILLATELSE, 234L);
    }

    @Test
    public void skal_kaste_exception_dersom_det_finnes_nyere_oppholdsoppgave_fra_for() {
        OppgaveImpl oppgaveSomBleOpprettetDagenFor = new OppgaveImpl(23, BRUKER.getAktorId(), OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 4, 9, 22, 0));
        List<OppgaveImpl> oppgaver = Collections.singletonList(oppgaveSomBleOpprettetDagenFor);

        when(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver);

        assertThrows(Feil.class, () -> oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE));

        verifyZeroInteractions(oppgaveGateway);
    }

    @Test
    public void skal_kaste_exception_dersom_det_finnes_nyere_utvandretoppgave_fra_for() {
        OppgaveImpl oppgaveSomBleOpprettetDagenFor = new OppgaveImpl(23, BRUKER.getAktorId(), UTVANDRET, 23, LocalDateTime.of(2020, 4, 9, 22, 0));
        List<OppgaveImpl> oppgaver = Collections.singletonList(oppgaveSomBleOpprettetDagenFor);

        when(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver);

        assertThrows(Feil.class, () -> oppgaveService.opprettOppgave(BRUKER, UTVANDRET));

        verifyZeroInteractions(oppgaveGateway);
    }

    @Test
    public void skal_ikke_kaste_exception_dersom_det_finnes_eldre_oppgave_fra_for() {
        when(oppgaveRouter.hentEnhetsnummerFor(BRUKER, OPPHOLDSTILLATELSE)).thenReturn(Optional.empty());
        OppgaveImpl oppgaveSomBleOpprettetTreDagerFor = new OppgaveImpl(23, BRUKER.getAktorId(), OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 3, 10, 22, 0));
        List<OppgaveImpl> oppgaver = Collections.singletonList(oppgaveSomBleOpprettetTreDagerFor);

        when(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver);
        when(oppgaveGateway.opprett(any())).thenReturn(new DummyOppgaveResponse());

        oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE);

        Oppgave oppgave = Oppgave.opprettOppgave(
                BRUKER.getAktorId(),
                null,
                OPPHOLDSTILLATELSE,
                LocalDate.of(2020, 4, 10));

        verify(oppgaveGateway, times(1)).opprett(oppgave);
    }

    @Test
    public void ingen_tidligere_oppgaver() {
        when(oppgaveRouter.hentEnhetsnummerFor(BRUKER, OPPHOLDSTILLATELSE)).thenReturn(Optional.empty());
        when(oppgaveRepository.hentOppgaverFor(any())).thenReturn(emptyList());
        when(oppgaveGateway.opprett(any())).thenReturn(new DummyOppgaveResponse());

        oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE);

        Oppgave oppgave = Oppgave.opprettOppgave(
                BRUKER.getAktorId(),
                null,
                OPPHOLDSTILLATELSE,
                LocalDate.of(2020, 4, 10));

        verify(oppgaveGateway, times(1)).opprett(oppgave);
    }

    @Test
    public void skal_ikke_kaste_exception_dersom_det_finnes_oppgave_av_annen_type() {
        when(oppgaveRouter.hentEnhetsnummerFor(BRUKER, OPPHOLDSTILLATELSE)).thenReturn(Optional.empty());
        OppgaveImpl oppgaveSomBleOpprettetEnDagerFor = new OppgaveImpl(23, BRUKER.getAktorId(), OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 4, 9, 22, 0));
        List<OppgaveImpl> oppgaver = Collections.singletonList(oppgaveSomBleOpprettetEnDagerFor);

        when(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver);
        when(oppgaveGateway.opprett(any())).thenReturn(new DummyOppgaveResponse());

        oppgaveService.opprettOppgave(BRUKER, UTVANDRET);

        Oppgave oppgave = Oppgave.opprettOppgave(
                BRUKER.getAktorId(),
                null,
                UTVANDRET,
                LocalDate.of(2020, 4, 10));

        verify(oppgaveGateway, times(1)).opprett(oppgave);
    }

    private static class DummyOppgaveResponse implements OppgaveResponse {

        @Override
        public long getId() {
            return 234L;
        }

        @Override
        public String getTildeltEnhetsnr() {
            return "0393";
        }
    }

    private static class CustomOppgaveService extends OppgaveService {

        public CustomOppgaveService(
                OppgaveGateway oppgaveGateway,
                OppgaveRepository oppgaveRepository,
                OppgaveRouter oppgaveRouter,
                KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer,
                BrukerRegistreringRepository brukerRegistreringRepository) {
            super(oppgaveGateway, oppgaveRepository, oppgaveRouter, kontaktBrukerHenvendelseProducer, brukerRegistreringRepository);
        }

        @Override
        protected LocalDate idag() {
            return LocalDate.of(2020, 4, 10);
        }
    }
}
