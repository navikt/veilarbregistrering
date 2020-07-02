package no.nav.fo.veilarbregistrering.enhet;

import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.enhet.Kommunenummer.KommuneMedBydel.*;
import static org.assertj.core.api.Assertions.assertThat;

public class KommunenummerTest {

    @Test
    public void oslo_er_en_kommune_med_flere_bydeler() {
        Kommunenummer osloKommune = Kommunenummer.of(OSLO);
        assertThat(osloKommune.kommuneMedBydeler()).isTrue();
    }

    @Test
    public void bergen_er_en_kommune_med_flere_bydeler() {
        Kommunenummer bergenKommune = Kommunenummer.of(BERGEN);
        assertThat(bergenKommune.kommuneMedBydeler()).isTrue();
    }

    @Test
    public void trondheim_er_en_kommune_med_flere_bydeler() {
        Kommunenummer trondheimKommune = Kommunenummer.of(TRONDHEIM);
        assertThat(trondheimKommune.kommuneMedBydeler()).isTrue();
    }

    @Test
    public void stavanger_er_en_kommune_med_flere_bydeler() {
        Kommunenummer stavangerKommune = Kommunenummer.of(STAVANGER);
        assertThat(stavangerKommune.kommuneMedBydeler()).isTrue();
    }

    @Test
    public void horten_er_ikke_en_kommune_med_flere_bydeler() {
        Kommunenummer hortenKommune = Kommunenummer.of("3801");
        assertThat(hortenKommune.kommuneMedBydeler()).isFalse();
    }
}
