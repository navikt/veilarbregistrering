package no.nav.fo.veilarbregistrering.bruker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FoedselsnummerTest {

    @Test
    public void maskert_skal_maskere_alle_tegn_med_stjerne() {
        Foedselsnummer foedselsnummer = Foedselsnummer.of("23067822521");
        assertThat(foedselsnummer.maskert()).isEqualTo("***********");
    }
}
