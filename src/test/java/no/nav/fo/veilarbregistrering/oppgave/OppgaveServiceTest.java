package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OppgaveServiceTest {

    private static final AktorId AKTOER_ID = AktorId.valueOf("123");

    private OppgaveService oppgaveService;

    private OppgaveGateway oppgaveGateway;
    private PersonGateway personGateway;

    @Before
    public void setup() {
        oppgaveGateway = Mockito.mock(OppgaveGateway.class);
        personGateway = Mockito.mock(PersonGateway.class);

        oppgaveService = new OppgaveService(oppgaveGateway, personGateway, (aktorId) -> {});
    }

    @Test
    public void skal_tilordne_oppgave_til_H134912_naar_geografisk_tilknytning_ikke_er_satt() {
        when(personGateway.hentGeografiskTilknytning(any(Foedselsnummer.class))).thenReturn(Optional.empty());
        when(oppgaveGateway.opprettOppgave(
                AKTOER_ID,
                "H134912",
                "Denne oppgaven har bruker selv opprettet, og er en pilotering på NAV Grünerløkka." +
                " Brukeren får ikke registrert seg som arbeidssøker." +
                " Kontaktperson ved NAV Grünerløkka er Marthe Harsvik."))
                .thenReturn(new Oppgave() {
            @Override
            public long getId() {
                return 213L;
            }

            @Override
            public String getTildeltEnhetsnr() {
                return "3242";
            }
        });

        oppgaveService.opprettOppgave(AKTOER_ID, Foedselsnummer.of("12345678910"));
    }

    @Test
    public void skal_tilordne_oppgave_til_H134912_naar_geografisk_tilknytning_er_030102() {
        when(personGateway.hentGeografiskTilknytning(any(Foedselsnummer.class))).thenReturn(Optional.empty());
        when(oppgaveGateway.opprettOppgave(
                AKTOER_ID,
                "H134912",
                "Denne oppgaven har bruker selv opprettet, og er en pilotering på NAV Grünerløkka." +
                        " Brukeren får ikke registrert seg som arbeidssøker." +
                        " Kontaktperson ved NAV Grünerløkka er Marthe Harsvik."))
                .thenReturn(new Oppgave() {
                    @Override
                    public long getId() {
                        return 213L;
                    }

                    @Override
                    public String getTildeltEnhetsnr() {
                        return "3242";
                    }
                });

        oppgaveService.opprettOppgave(AKTOER_ID, Foedselsnummer.of("12345678910"));
    }

    @Test
    public void skal_tilordne_oppgave_til_H134912_naar_geografisk_tilknytning_ikke_er_mappet_opp() {
        when(personGateway.hentGeografiskTilknytning(any(Foedselsnummer.class))).thenReturn(Optional.of(GeografiskTilknytning.of("030105")));
        when(oppgaveGateway.opprettOppgave(
                AKTOER_ID,
                "H134912",
                "Denne oppgaven har bruker selv opprettet, og er en pilotering på NAV Grünerløkka." +
                        " Brukeren får ikke registrert seg som arbeidssøker." +
                        " Kontaktperson ved NAV Grünerløkka er Marthe Harsvik."))
                .thenReturn(new Oppgave() {
                    @Override
                    public long getId() {
                        return 213L;
                    }

                    @Override
                    public String getTildeltEnhetsnr() {
                        return "3242";
                    }
                });

        oppgaveService.opprettOppgave(AKTOER_ID, Foedselsnummer.of("12345678910"));
    }

    @Test
    public void skal_tilordne_oppgave_til_B125772_naar_geografisk_tilknytning_er_3411() {
        when(personGateway.hentGeografiskTilknytning(any(Foedselsnummer.class))).thenReturn(Optional.of(GeografiskTilknytning.of("3411")));
        when(oppgaveGateway.opprettOppgave(
                AKTOER_ID,
                "B125772",
                "Denne oppgaven har bruker selv opprettet, og er en pilotering på NAV Ringsaker." +
                        " Brukeren får ikke registrert seg som arbeidssøker." +
                        " Kontaktperson ved NAV Ringsaker er Inger Johanne Bryn."))
                .thenReturn(new Oppgave() {
                    @Override
                    public long getId() {
                        return 213L;
                    }

                    @Override
                    public String getTildeltEnhetsnr() {
                        return "3242";
                    }
                });

        oppgaveService.opprettOppgave(AKTOER_ID, Foedselsnummer.of("12345678910"));
    }
}
