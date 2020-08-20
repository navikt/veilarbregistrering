package no.nav.fo.veilarbregistrering.bruker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GeografiskTilknytningTest {

    @Test
    public void geografiskTilknytning_kan_ikke_vaere_null() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> GeografiskTilknytning.of(null));
        assertThat(nullPointerException.getMessage()).isEqualTo("Geografisk tilknytning kan ikke være null. Bruk <code>ofNullable</code> hvis du er usikker.");
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
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> GeografiskTilknytning.of("12").value());
        assertThat(illegalArgumentException.getMessage()).isEqualTo("Geografisk tilknytning har ukjent format: 12");
    }

    @Test
    public void bydel_bjerke_i_oslo_er_ikke_by_med_bydel() {
        GeografiskTilknytning bjerke = GeografiskTilknytning.of(GeografiskTilknytning.BydelOslo.Bjerke.kode());
        assertThat(bjerke.byMedBydeler()).isFalse();
    }

    @Test
    public void oslo_er_by_med_bydeler() {
        GeografiskTilknytning oslo = GeografiskTilknytning.of(GeografiskTilknytning.ByMedBydeler.Oslo.kode());
        assertThat(oslo.byMedBydeler()).isTrue();
    }

    @Test
    public void stavanger_er_by_med_bydeler() {
        GeografiskTilknytning oslo = GeografiskTilknytning.of(GeografiskTilknytning.ByMedBydeler.Stavanger.kode());
        assertThat(oslo.byMedBydeler()).isTrue();
    }

    @Test
    public void bergen_er_by_med_bydeler() {
        GeografiskTilknytning oslo = GeografiskTilknytning.of(GeografiskTilknytning.ByMedBydeler.Bergen.kode());
        assertThat(oslo.byMedBydeler()).isTrue();
    }

    @Test
    public void trondheim_er_by_med_bydeler() {
        GeografiskTilknytning oslo = GeografiskTilknytning.of(GeografiskTilknytning.ByMedBydeler.Trondheim.kode());
        assertThat(oslo.byMedBydeler()).isTrue();
    }

}
