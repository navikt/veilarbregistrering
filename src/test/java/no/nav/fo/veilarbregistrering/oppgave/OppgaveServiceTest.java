package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OppgaveServiceTest {

    public static final Bruker BRUKER = Bruker.of(
            Foedselsnummer.of("12345678911"),
            AktorId.valueOf("2134"));

    @Test
    public void opprettOppgave_ang_opphold_skal_gi_beskrivelse_om_rutine() {
        OppgaveService oppgaveService = new OppgaveService((aktoerId, beskrivelse) -> {

            assertThat(beskrivelse).isEqualTo("Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                    "og har selv opprettet denne oppgaven. " +
                    "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.");

            return new Oppgave() {
                @Override
                public long getId() {
                    return 324;
                }

                @Override
                public String getTildeltEnhetsnr() {
                    return "0125";
                }
            };
        }, aktorId -> {
        });

        oppgaveService.opprettOppgave(BRUKER, OppgaveType.OPPHOLDSTILLATELSE);
    }
}
