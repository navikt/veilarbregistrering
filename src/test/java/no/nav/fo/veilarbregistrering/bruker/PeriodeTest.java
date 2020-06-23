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

    @Test
    public void tildato_er_innenfor_forespurt_periode() {
        // [januar februar mars april]
        // -------[februar mars april mai]
        Periode periodeMedTildato = Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 4, 1));
        Periode forespurtPeriode = Periode.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 5, 1));

        assertThat(periodeMedTildato.overlapperMed(forespurtPeriode)).isTrue();
    }

    @Test
    public void periode_er_avs() {
        Periode periodeMedTildato = Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31));
        Periode forespurtPeriode = Periode.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 5, 1));

        assertThat(periodeMedTildato.overlapperMed(forespurtPeriode)).isFalse();
    }

    @Test
    public void periode_ar_avs() {
        Periode periodeMedTildato = Periode.of(LocalDate.of(2020, 6, 1), LocalDate.of(2020, 8, 31));
        Periode forespurtPeriode = Periode.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 5, 1));

        assertThat(periodeMedTildato.overlapperMed(forespurtPeriode)).isFalse();
    }

    //@Test
    public void forespurtPeriode_med_tildato_lik_null() {
        Periode periodeMedTildato = Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 4, 1));
        Periode forespurtPeriode = Periode.of(LocalDate.of(2020, 2, 1), null);

        assertThat(periodeMedTildato.overlapperMed(forespurtPeriode)).isTrue();
    }
}
