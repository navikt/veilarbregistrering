package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class OppgaveServiceTest {

    private static final Bruker BRUKER = Bruker.of(
            Foedselsnummer.of("12345678911"),
            AktorId.valueOf("2134"));

    private OppgaveService oppgaveService;

    private OppgaveGateway oppgaveGateway;
    private OppgaveRepository oppgaveRepository;
    private UnleashService unleashService;

    @Before
    public void setUp() {
        oppgaveGateway = mock(OppgaveGateway.class);
        oppgaveRepository = mock(OppgaveRepository.class);
        unleashService = mock(UnleashService.class);
        oppgaveService = new OppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                aktorId -> {
                },
                unleashService);
    }

    @Test
    public void opprettOppgave_ang_opphold_skal_gi_beskrivelse_om_rutine() {
        when(oppgaveGateway.opprettOppgave(any(), any())).thenReturn(new DummyOppgaveResponse());

        oppgaveService.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE);

        verify(oppgaveGateway, times(1)).opprettOppgave(BRUKER.getAktorId(), "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                "og har selv opprettet denne oppgaven. " +
                "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.");
    }

    @Test
    public void skal_lagre_oppgave_ved_vellykket_opprettelse_av_oppgave() {
        when(oppgaveGateway.opprettOppgave(any(), any())).thenReturn(new DummyOppgaveResponse());
        oppgaveService.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE);
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
}
