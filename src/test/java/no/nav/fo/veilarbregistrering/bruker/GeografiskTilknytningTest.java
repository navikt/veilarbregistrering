package no.nav.fo.veilarbregistrering.bruker;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeografiskTilknytningTest {

    @Test
    public void geografiskTilknytning_kan_ikke_vaere_null() {
        try {
            GeografiskTilknytning.of(null);
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("Geografisk tilknytning kan ikke være null. Bruk <code>ofNullable</code> hvis du er usikker.");
        }
    }

    @Test
    public void fieldName_skal_bruke_bydel_ikke_oslo_hvis_geografiskTilknytning_er_bydel_men_ikke_Oslo() {
        assertThat(GeografiskTilknytning.of("123456").value()).isEqualTo("bydelIkkeOslo");
    }

    @Test
    public void fieldName_skal_bruke_bydel_oslo_med_bydelskode_hvis_geografiskTilknytning_er_bydel_i_Oslo() {
        assertThat(GeografiskTilknytning.of("030106").value()).isEqualTo("bydelOsloUllern");
    }

    @Test
    public void fieldName_skal_bruke_fylke_hvis_geografiskTilknytning_består_av_fire_siffer() {
        assertThat(GeografiskTilknytning.of("1234").value()).isEqualTo("fylke");
    }

    @Test
    public void fieldName_skal_bruke_utland_hvis_geografiskTilknytning_består_av_tre_bokstaver() {
        assertThat(GeografiskTilknytning.of("ABC").value()).isEqualTo("utland");
    }

    @Test
    public void exception_skal_kastes_hvis_geografiskTilknytning_er_ukjent() {
        try {
            GeografiskTilknytning.of("12").value();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Geografisk tilknytning har ukjent format: 12");
        }
    }
}
