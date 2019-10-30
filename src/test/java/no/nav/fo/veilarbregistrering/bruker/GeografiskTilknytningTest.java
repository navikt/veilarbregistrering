package no.nav.fo.veilarbregistrering.bruker;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeografiskTilknytningTest {

    @Test
    public void fieldName_skal_bruke_bydel_ikke_oslo_hvis_geografiskTilknytning_er_bydel_men_ikke_Oslo() {
        assertThat(GeografiskTilknytning.of("123456").fiedldName()).isEqualTo("bydel.ikke.oslo");
    }

    @Test
    public void fieldName_skal_bruke_bydel_oslo_med_bydelskode_hvis_geografiskTilknytning_er_bydel_i_Oslo() {
        assertThat(GeografiskTilknytning.of("030106").fiedldName()).isEqualTo("bydel.oslo.030106");
    }

    @Test
    public void fieldName_skal_bruke_fylke_hvis_geografiskTilknytning_består_av_fire_siffer() {
        assertThat(GeografiskTilknytning.of("1234").fiedldName()).isEqualTo("fylke");
    }

    @Test
    public void fieldName_skal_bruke_utland_hvis_geografiskTilknytning_består_av_tre_bokstaver() {
        assertThat(GeografiskTilknytning.of("ABC").fiedldName()).isEqualTo("utland");
    }

    @Test
    public void exception_skal_kastes_hvis_geografiskTilknytning_er_ukjent() {
        try {
            GeografiskTilknytning.of("12").fiedldName();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Geografisk tilknytning har ukjent format: 12");
        }
    }
}
