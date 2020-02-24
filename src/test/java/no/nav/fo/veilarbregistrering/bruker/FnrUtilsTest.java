package no.nav.fo.veilarbregistrering.bruker;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static no.bekk.bekkopen.person.FodselsnummerCalculator.getFodselsnummerForDate;
import static no.nav.fo.veilarbregistrering.bruker.FnrUtils.antallAarSidenDato;
import static no.nav.fo.veilarbregistrering.bruker.FnrUtils.utledFodselsdatoForFnr;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class FnrUtilsTest {

    private static final LocalDate dagensDato = LocalDate.of(2017,12,14);

    public static String getFodselsnummerOnDateMinusYears(LocalDate localDate, int minusYears) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).minusYears(minusYears).toInstant());
        return getFodselsnummerForDate(date).toString();
    }

    @Test
    public void skalUtledeKorrektFodselsdato() {
        String fnr = getFodselsnummerOnDateMinusYears(dagensDato, 40);
        assertThat(utledFodselsdatoForFnr(fnr)).isEqualTo(LocalDate.of(1977,12,14));
    }

    @Test
    public void skalVaere20Aar() {
        LocalDate dato = LocalDate.of(1997,12,14);
        assertThat(antallAarSidenDato(dato, dagensDato)).isEqualTo(20);
    }

    @Test
    public void skalVaere20Aar_2() {
        LocalDate dato = LocalDate.of(1997, 1, 1);
        assertThat(antallAarSidenDato(dato, dagensDato)).isEqualTo(20);
    }

    @Test
    public void skalVaere19Aar() {
        LocalDate dato = LocalDate.of(1997, 12, 15);
        assertThat(antallAarSidenDato(dato, dagensDato)).isEqualTo(19);
    }

    @Test
    public void skalVaere19Aar_2() {
        LocalDate dato = LocalDate.of(1998, 12, 14);
        assertThat(antallAarSidenDato(dato, dagensDato)).isEqualTo(19);
    }
    @Test
    public void skalVaere1Aar() {
        LocalDate dato = LocalDate.of(2016, 2, 29);
        LocalDate dagensDato = LocalDate.of(2018,2,28);
        assertThat(antallAarSidenDato(dato, dagensDato)).isEqualTo(1);
    }
    @Test
    public void skalVaere2Aar() {
        LocalDate dato = LocalDate.of(2016, 2, 29);
        LocalDate dagensDato = LocalDate.of(2018,3,1);
        assertThat(antallAarSidenDato(dato, dagensDato)).isEqualTo(2);
    }
}