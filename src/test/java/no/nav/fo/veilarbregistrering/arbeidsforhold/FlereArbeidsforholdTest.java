package no.nav.fo.veilarbregistrering.arbeidsforhold;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdataBuilder.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class FlereArbeidsforholdTest {

    @Test
    public void skalHenteSisteEllerPaagendeArbeidsforhold() {

        assertThat(FlereArbeidsforholdTestdataBuilder.flereArbeidsforholdTilfeldigSortert().siste()).isEqualTo(siste());

        List<Arbeidsforhold> stigendeSortertListe = asList(eldre(), nestSiste(), siste());
        assertThat(FlereArbeidsforhold.of(stigendeSortertListe).siste()).isEqualTo(siste());

        List<Arbeidsforhold> synkendeSortertListe = asList(siste(), nestSiste(), eldre());
        assertThat(FlereArbeidsforhold.of(synkendeSortertListe).siste()).isEqualTo(siste());

        // Skal hente paagendeArbeidsforhold
        List<Arbeidsforhold> tilfeldigSortertListeMedPaagaaende = asList(eldre(), paagaaende(), siste(), nestSiste());
        assertThat(FlereArbeidsforhold.of(tilfeldigSortertListeMedPaagaaende).siste()).isEqualTo(paagaaende());

        List<Arbeidsforhold> stigendeSortertListePaagaande = asList(eldre(), nestSiste(), siste(), paagaaende());
        assertThat(FlereArbeidsforhold.of(stigendeSortertListePaagaande).siste()).isEqualTo(paagaaende());

        List<Arbeidsforhold> synkendeSortertListePaagaande = asList(paagaaende(), siste(), nestSiste(), eldre());
        assertThat(FlereArbeidsforhold.of(synkendeSortertListePaagaande).siste()).isEqualTo(paagaaende());
    }

    @Test
    public void skalHenteLengsteAvPaagendeArbeidsforhold() {
        LocalDate fom3 = LocalDate.of(2017,1,1);
        LocalDate tom3 = null;
        LocalDate fom2 = LocalDate.of(2017,10,1);
        LocalDate tom2 = null;
        LocalDate fom1 = LocalDate.of(2017,11,1);
        LocalDate tom1 = null;

        Arbeidsforhold paagaaendeArbeidsforholdVarighet3 = ArbeidsforholdTestdataBuilder.medDato(fom3, tom3);
        Arbeidsforhold paagaaendeArbeidsforholdVarighet2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2);
        Arbeidsforhold paagaaendeArbeidsforholdVarighet1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1);

        List<Arbeidsforhold> flerePaagendeArbeidsforhold =
                asList(paagaaendeArbeidsforholdVarighet2, paagaaendeArbeidsforholdVarighet1, paagaaendeArbeidsforholdVarighet3);
        assertThat(FlereArbeidsforhold.of(flerePaagendeArbeidsforhold).siste()).isEqualTo(paagaaendeArbeidsforholdVarighet3);
    }

    @Test
    public void skalHenteLengsteAvSisteArbeidsforhold() {
        LocalDate fom3 = LocalDate.of(2017,1,1);
        LocalDate tom3 = LocalDate.of(2017,11,30);
        LocalDate fom2 = LocalDate.of(2017,10,1);
        LocalDate tom2 = LocalDate.of(2017,11,30);
        LocalDate fom1 = LocalDate.of(2017,11,1);
        LocalDate tom1 = LocalDate.of(2017,11,30);

        Arbeidsforhold sisteArbeidsforholdVarighet3 = ArbeidsforholdTestdataBuilder.medDato(fom3, tom3);
        Arbeidsforhold sisteArbeidsforholdvarighet2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2);
        Arbeidsforhold sisteArbeidsforholdVarighet1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1);

        List<Arbeidsforhold> flereSisteArbeidsforhold =
                asList(sisteArbeidsforholdVarighet1, sisteArbeidsforholdvarighet2, sisteArbeidsforholdVarighet3);
        assertThat(FlereArbeidsforhold.of(flereSisteArbeidsforhold).siste()).isEqualTo(sisteArbeidsforholdVarighet3);
    }

    @Test
    public void skalDefaultArbeidsforhold() {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold(null, "utenstyrkkode", null, null, null);
        assertThat(FlereArbeidsforhold.of(Collections.emptyList()).siste()).isEqualTo(arbeidsforhold);
    }

    @Test
    public void nullArbeidsforholdSkalHåndteresSomTomListe() {
        assertThat(FlereArbeidsforhold.of(null).siste()).isEqualTo(FlereArbeidsforhold.of(Collections.emptyList()).siste());
    }

    @Test
    public void skalHaArbeidsforholdPaaDato() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom1 = LocalDate.of(2017,10,1);
        LocalDate tom1 = LocalDate.of(2017,12,1);
        LocalDate fom2 = LocalDate.of(2017,12,1);
        LocalDate tom2 = LocalDate.of(2017,12,30);
        Arbeidsforhold arbeidsforhold1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1);
        Arbeidsforhold arbeidsforhold2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2);
        List<Arbeidsforhold> arbeidsforhold = asList(arbeidsforhold1, arbeidsforhold2);

        assertThat(FlereArbeidsforhold.of(arbeidsforhold).harArbeidsforholdPaaDato(mnd)).isTrue();
    }

    @Test
    public void skalIkkeHaArbeidsforholdPaaDato() {
        LocalDate mnd = LocalDate.of(2018,12,1);
        LocalDate fom1 = LocalDate.of(2017,10,1);
        LocalDate tom1 = LocalDate.of(2017,12,1);
        LocalDate fom2 = LocalDate.of(2017,12,1);
        LocalDate tom2 = LocalDate.of(2017,12,30);
        Arbeidsforhold arbeidsforhold1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1);
        Arbeidsforhold arbeidsforhold2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2);
        List<Arbeidsforhold> arbeidsforhold = asList(arbeidsforhold1, arbeidsforhold2);

        assertThat(FlereArbeidsforhold.of(arbeidsforhold).harArbeidsforholdPaaDato(mnd)).isFalse();
    }

    @Test
    public void skalVaereIJobb2av4Mnd() {
        FlereArbeidsforhold.antallMnd = 4;
        FlereArbeidsforhold.minAntallMndSammenhengendeJobb = 2;
        LocalDate dagensDato = LocalDate.of(2017,12,20);

        LocalDate mnd = LocalDate.of(2018,12,1);
        LocalDate fom1 = LocalDate.of(2017,10,1);
        LocalDate tom1 = LocalDate.of(2017,10,31);
        LocalDate fom2 = LocalDate.of(2017,9,1);
        LocalDate tom2 = LocalDate.of(2017,9,30);
        Arbeidsforhold arbeidsforhold1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1);
        Arbeidsforhold arbeidsforhold2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2);
        List<Arbeidsforhold> arbeidsforhold = asList(arbeidsforhold1, arbeidsforhold2);

        assertThat(FlereArbeidsforhold.of(arbeidsforhold).harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato)).isTrue();
    }

    @Test
    public void skalIkkeVaereIJobb2av4Mnd() {
        FlereArbeidsforhold.antallMnd = 4;
        FlereArbeidsforhold.minAntallMndSammenhengendeJobb = 2;
        LocalDate dagensDato = LocalDate.of(2017,12,20);

        LocalDate mnd = LocalDate.of(2018,12,1);
        LocalDate fom1 = LocalDate.of(2017,11,1);
        LocalDate tom1 = LocalDate.of(2017,11,30);
        LocalDate fom2 = LocalDate.of(2017,9,1);
        LocalDate tom2 = LocalDate.of(2017,9,30);
        Arbeidsforhold arbeidsforhold1 = ArbeidsforholdTestdataBuilder.medDato(fom1, tom1);
        Arbeidsforhold arbeidsforhold2 = ArbeidsforholdTestdataBuilder.medDato(fom2, tom2);
        List<Arbeidsforhold> arbeidsforhold = asList(arbeidsforhold1, arbeidsforhold2);

        assertThat(FlereArbeidsforhold.of(arbeidsforhold).harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato)).isFalse();
    }
}
