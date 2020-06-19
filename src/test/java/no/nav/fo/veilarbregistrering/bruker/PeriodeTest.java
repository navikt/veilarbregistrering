package no.nav.fo.veilarbregistrering.bruker;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class PeriodeTest {

    @Test
    public void fraDatoAs_yyyyMMdd_skal_skrive_ut_fradato_pa_formatet_yyyyMMdd() {
        Periode periode = Periode.of(LocalDate.of(2020, 1, 12), LocalDate.of(2020, 2, 20));
        assertThat(periode.fraDatoAs_yyyyMMdd()).isEqualTo("2020-01-12");
    }

    @Test
    public void tilDatoAs_yyyyMMdd_skal_skrive_ut_tildato_pa_formatet_yyyyMMdd() {
        Periode periode = Periode.of(LocalDate.of(2020, 1, 12), LocalDate.of(2020, 2, 20));
        assertThat(periode.tilDatoAs_yyyyMMdd()).isEqualTo("2020-02-20");
    }
}
