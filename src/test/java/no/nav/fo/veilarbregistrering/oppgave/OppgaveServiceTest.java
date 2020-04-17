package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.apiapp.feil.Feil;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.fo.veilarbregistrering.oppgave.OppgaveType.OPPHOLDSTILLATELSE;
import static org.mockito.Mockito.*;

public class OppgaveServiceTest {

    private static final Bruker BRUKER = Bruker.of(
            Foedselsnummer.of("12345678911"),
            AktorId.valueOf("2134"));

    private OppgaveService oppgaveService;

    private OppgaveGateway oppgaveGateway;
    private OppgaveRepository oppgaveRepository;

    @Before
    public void setUp() {
        oppgaveGateway = mock(OppgaveGateway.class);
        oppgaveRepository = mock(OppgaveRepository.class);
        oppgaveService = new CustomOppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                aktorId -> {
                });
    }

    @Test
    public void opprettOppgave_ang_opphold_skal_gi_beskrivelse_om_rutine() {
        when(oppgaveGateway.opprettOppgave(any(), any())).thenReturn(new DummyOppgaveResponse());

        oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE);

        verify(oppgaveGateway, times(1)).opprettOppgave(BRUKER.getAktorId(), "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                "og har selv opprettet denne oppgaven. " +
                "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.");
    }

    @Test
    public void skal_lagre_oppgave_ved_vellykket_opprettelse_av_oppgave() {
        when(oppgaveGateway.opprettOppgave(any(), any())).thenReturn(new DummyOppgaveResponse());
        oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE);

        verify(oppgaveRepository, times(1))
                .opprettOppgave(BRUKER.getAktorId(), OPPHOLDSTILLATELSE, 234L);
    }

    @Test(expected = Feil.class)
    public void skal_kaste_exception_dersom_det_finnes_nyere_oppgave_fra_for() {
        OppgaveImpl oppgaveSomBleOpprettetDagenFor = new OppgaveImpl(23, BRUKER.getAktorId(), OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 4, 9, 22, 0));
        List<OppgaveImpl> oppgaver = Collections.singletonList(oppgaveSomBleOpprettetDagenFor);

        when(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver);

        oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE);

        verifyZeroInteractions(oppgaveGateway);
    }

    @Test
    public void skal_ikke_kaste_exception_dersom_det_finnes_eldre_oppgave_fra_for() {
        OppgaveImpl oppgaveSomBleOpprettetTreDagerFor = new OppgaveImpl(23, BRUKER.getAktorId(), OPPHOLDSTILLATELSE, 23, LocalDateTime.of(2020, 3, 10, 22, 0));
        List<OppgaveImpl> oppgaver = Collections.singletonList(oppgaveSomBleOpprettetTreDagerFor);

        when(oppgaveRepository.hentOppgaverFor(any())).thenReturn(oppgaver);
        when(oppgaveGateway.opprettOppgave(any(), any())).thenReturn(new DummyOppgaveResponse());

        oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE);

        verify(oppgaveGateway, times(1)).opprettOppgave(BRUKER.getAktorId(), "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                "og har selv opprettet denne oppgaven. " +
                "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.");
    }

    @Test
    public void ingen_tidligere_oppgaver() {
        when(oppgaveRepository.hentOppgaverFor(any())).thenReturn(emptyList());
        when(oppgaveGateway.opprettOppgave(any(), any())).thenReturn(new DummyOppgaveResponse());

        oppgaveService.opprettOppgave(BRUKER, OPPHOLDSTILLATELSE);

        verify(oppgaveGateway, times(1)).opprettOppgave(BRUKER.getAktorId(), "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                "og har selv opprettet denne oppgaven. " +
                "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.");
    }

    private static class DummyOppgaveResponse implements Oppgave {

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

        public CustomOppgaveService(OppgaveGateway oppgaveGateway, OppgaveRepository oppgaveRepository, KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer) {
            super(oppgaveGateway, oppgaveRepository, kontaktBrukerHenvendelseProducer);
        }

        @Override
        protected LocalDate idag() {
            return LocalDate.of(2020, 4, 10);
        }
    }
}
