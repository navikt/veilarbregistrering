package no.nav.fo.veilarbregistrering.sykemelding;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class MaksdatoTest {

    @Test
    public void skalVaereSykmeldtOverEllerLik39Uker() {
        String maksDato = "2018-10-01";
        LocalDate dagenDato = LocalDate.of(2018, Month.JUNE, 26);
        assertTrue(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato));
    }

    @Test
    public void skalVaereSykmeldtAkkurat52Uker() {
        String maksDato = "2018-12-11";
        LocalDate dagenDato = LocalDate.of(2018, Month.DECEMBER, 11);
        assertTrue(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato));
    }

    @Test
    public void skalVaereSykmeldtNesten52Uker() {
        String maksDato = "2018-12-11";
        LocalDate dagenDato = LocalDate.of(2018, Month.DECEMBER, 9);
        assertTrue(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato));
    }

    @Test
    public void skalIkkeVaereSykmeldtOver39Uker() {
        String maksDato = "2018-10-01";
        LocalDate dagenDato = LocalDate.of(2018, Month.APRIL, 9);
        assertFalse(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato));
    }

    @Test
    public void skalIkkeVaereSykmeldtOver39UkerNarMaksDatoErUnderDagensDato() {
        String maksDato = "2018-10-01";
        LocalDate dagenDato = LocalDate.of(2019, Month.APRIL, 9);
        assertFalse(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato));
    }

    @Test(expected = NullPointerException.class)
    public void skalHandtereNullVedBeregnSykmeldtOver39uker() {
        String maksDato = null;
        LocalDate dagenDato = LocalDate.of(2019, Month.APRIL, 9);
        assertFalse(Maksdato.of(maksDato).beregnSykmeldtMellom39Og52Uker(dagenDato));
    }

    @Test
    public void antallUkerSykmeldt_er_negativt_mer_enn_ett_år_tidligere() {
        LocalDate dagenDato = LocalDate.of(2019, Month.JANUARY, 1);
        Maksdato maksdato = Maksdato.of("2020-03-01");

        Long antallUkerSykmeldt = maksdato.antallUkerSykmeldt(dagenDato);

        assertThat(antallUkerSykmeldt).isEqualTo(-8);
    }

    @Test
    public void antallUkerSykmeldt_er_0_ved_start_sykepenger() {
        LocalDate dagenDato = LocalDate.of(2019, Month.MARCH, 1);
        Maksdato maksdato = Maksdato.of("2020-03-01");

        Long antallUkerSykmeldt = maksdato.antallUkerSykmeldt(dagenDato);

        assertThat(antallUkerSykmeldt).isEqualTo(0);
    }

    @Test
    public void antallUkerSykmeldt_er_1_etter_en_uke_med_sykepenger() {
        LocalDate dagenDato = LocalDate.of(2019, Month.MARCH, 8);
        Maksdato maksdato = Maksdato.of("2020-03-01");

        Long antallUkerSykmeldt = maksdato.antallUkerSykmeldt(dagenDato);

        assertThat(antallUkerSykmeldt).isEqualTo(1);
    }

    @Test
    public void antallUkerSykmeldt_er_52_når_dagens_dato_er_lik_maksdato() {
        LocalDate dagenDato = LocalDate.of(2020, Month.MARCH, 1);
        Maksdato maksdato = Maksdato.of("2020-03-01");

        Long antallUkerSykmeldt = maksdato.antallUkerSykmeldt(dagenDato);

        assertThat(antallUkerSykmeldt).isEqualTo(52);
    }
}
