package no.nav.fo.veilarbregistrering.bruker;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class FoedselsdatoTest {

    @Test
    public void alder_oker_ikke_for_foedselsdag() {
        Foedselsdato foedselsdato = new Foedselsdato(LocalDate.of(1978, 6, 23)) {

            @Override
            protected LocalDate dagensDato() {
                return LocalDate.of(2020, 2, 20);
            }
        };

        assertThat(foedselsdato.alder()).isEqualTo(41);
    }

    @Test
    public void foedselsdag_oker_alder() {
        Foedselsdato foedselsdato = new Foedselsdato(LocalDate.of(1978, 6, 23)) {

            @Override
            protected LocalDate dagensDato() {
                return LocalDate.of(2020, 6, 23);
            }
        };

        assertThat(foedselsdato.alder()).isEqualTo(42);
    }
}
