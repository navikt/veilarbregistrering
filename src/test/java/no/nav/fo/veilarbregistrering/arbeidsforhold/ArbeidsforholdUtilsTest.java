package no.nav.fo.veilarbregistrering.arbeidsforhold;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class ArbeidsforholdUtilsTest {

    @Test
    public void skalVaereIJobb2av4Mnd() {
        ArbeidsforholdUtils.antallMnd = 4;
        ArbeidsforholdUtils.minAntallMndSammenhengendeJobb = 2;
        LocalDate dagensDato = LocalDate.of(2017,12,20);

        LocalDate mnd = LocalDate.of(2018,12,1);
        LocalDate fom1 = LocalDate.of(2017,10,1);
        LocalDate tom1 = LocalDate.of(2017,10,31);
        LocalDate fom2 = LocalDate.of(2017,9,1);
        LocalDate tom2 = LocalDate.of(2017,9,30);
        Arbeidsforhold arbeidsforhold1 = new Arbeidsforhold().setFom(fom1).setTom(tom1);
        Arbeidsforhold arbeidsforhold2 = new Arbeidsforhold().setFom(fom2).setTom(tom2);
        List<Arbeidsforhold> arbeidsforhold = asList(arbeidsforhold1, arbeidsforhold2);

        assertThat(harJobbetSammenhengendeSeksAvTolvSisteManeder(FlereArbeidsforhold.of(arbeidsforhold), dagensDato)).isTrue();
    }

    @Test
    public void skalIkkeVaereIJobb2av4Mnd() {
        ArbeidsforholdUtils.antallMnd = 4;
        ArbeidsforholdUtils.minAntallMndSammenhengendeJobb = 2;
        LocalDate dagensDato = LocalDate.of(2017,12,20);

        LocalDate mnd = LocalDate.of(2018,12,1);
        LocalDate fom1 = LocalDate.of(2017,11,1);
        LocalDate tom1 = LocalDate.of(2017,11,30);
        LocalDate fom2 = LocalDate.of(2017,9,1);
        LocalDate tom2 = LocalDate.of(2017,9,30);
        Arbeidsforhold arbeidsforhold1 = new Arbeidsforhold().setFom(fom1).setTom(tom1);
        Arbeidsforhold arbeidsforhold2 = new Arbeidsforhold().setFom(fom2).setTom(tom2);
        List<Arbeidsforhold> arbeidsforhold = asList(arbeidsforhold1, arbeidsforhold2);

        assertThat(harJobbetSammenhengendeSeksAvTolvSisteManeder(FlereArbeidsforhold.of(arbeidsforhold), dagensDato)).isFalse();
    }

}