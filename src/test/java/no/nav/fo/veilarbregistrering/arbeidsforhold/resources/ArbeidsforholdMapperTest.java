package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ArbeidsforholdMapperTest {

    @Test
    public void skalMappeAlleVerdier() {
        LocalDate fom = LocalDate.of(2017,12,1);
        LocalDate tom = LocalDate.of(2017,12,30);
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold("453542352", "STK", fom, tom);

        ArbeidsforholdDto arbeidsforholdDto = ArbeidsforholdMapper.map(arbeidsforhold);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(arbeidsforholdDto.getArbeidsgiverOrgnummer()).isEqualTo("453542352");
        softAssertions.assertThat(arbeidsforholdDto.getStyrk()).isEqualTo("STK");
        softAssertions.assertThat(arbeidsforholdDto.getFom()).isEqualTo(fom);
        softAssertions.assertThat(arbeidsforholdDto.getTom()).isEqualTo(tom);

        softAssertions.assertAll();
    }
}
