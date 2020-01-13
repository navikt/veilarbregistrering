package no.nav.fo.veilarbregistrering.oppgave;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NavKontorTest {

    @Test
    public void marthe_skal_vaere_del_av_beskrivelse() {
        NavKontor navKontor = NavKontor.grünerlokka();
        String beskrivelse = navKontor.beskrivelse();

        assertThat(beskrivelse).isEqualTo("Denne oppgaven har bruker selv opprettet, og er en pilotering på NAV Grünerløkka." +
                " Brukeren får ikke registrert seg som arbeidssøker." +
                " Kontaktperson ved NAV Grünerløkka er Marthe Harsvik.");
    }

    @Test
    public void nav_grünerloekka_skal_vaere_del_av_beskrivelse() {
        NavKontor navKontor = NavKontor.grünerlokka();
        String beskrivelse = navKontor.beskrivelse();

        assertThat(beskrivelse).contains("NAV Grünerløkka");
    }

}
