package no.nav.fo.veilarbregistrering.arbeidsforhold;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class ArbeidsforholdTest {

    @Test
    public void datoSkalVaereInneforPeriodeNaarTomErNull() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom = LocalDate.of(2010,12,1);
        Arbeidsforhold arbeidsforhold = ArbeidsforholdTestdataBuilder.medDato(fom, null);
        assertThat(arbeidsforhold.erDatoInnenforPeriode(mnd)).isTrue();
    }

    @Test
    public void datoSkalVaereInneforPeriode() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom = LocalDate.of(2017,12,1);
        LocalDate tom = LocalDate.of(2017,12,30);
        Arbeidsforhold arbeidsforhold = ArbeidsforholdTestdataBuilder.medDato(fom, tom);
        assertThat(arbeidsforhold.erDatoInnenforPeriode(mnd)).isTrue();
    }

    @Test
    public void datoSkalVaereInneforPeriode_2() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom = LocalDate.of(2017,10,1);
        LocalDate tom = LocalDate.of(2017,12,1);
        Arbeidsforhold arbeidsforhold = ArbeidsforholdTestdataBuilder.medDato(fom, tom);
        assertThat(arbeidsforhold.erDatoInnenforPeriode(mnd)).isTrue();
    }

    @Test
    public void datoSkalIkkeVaereInneforPeriode() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom = LocalDate.of(2017,9,1);
        LocalDate tom = LocalDate.of(2017,11,30);
        Arbeidsforhold arbeidsforhold = ArbeidsforholdTestdataBuilder.medDato(fom, tom);
        assertThat(arbeidsforhold.erDatoInnenforPeriode(mnd)).isFalse();
    }

}
