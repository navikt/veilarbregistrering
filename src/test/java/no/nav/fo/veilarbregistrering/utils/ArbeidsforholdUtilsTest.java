package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.fo.veilarbregistrering.utils.ArbeidsforholdUtils.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class ArbeidsforholdUtilsTest {

    @Test
    public void datoSkalVaereInneforPeriodeNaarTomErNull() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom = LocalDate.of(2010,12,1);
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold().setFom(fom);
        assertThat(erDatoInnenforPeriode(arbeidsforhold,mnd)).isTrue();
    }

    @Test
    public void datoSkalVaereInneforPeriode() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom = LocalDate.of(2017,12,1);
        LocalDate tom = LocalDate.of(2017,12,30);
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold().setFom(fom).setTom(tom);
        assertThat(erDatoInnenforPeriode(arbeidsforhold,mnd)).isTrue();
    }

    @Test
    public void datoSkalVaereInneforPeriode_2() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom = LocalDate.of(2017,10,1);
        LocalDate tom = LocalDate.of(2017,12,1);
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold().setFom(fom).setTom(tom);
        assertThat(erDatoInnenforPeriode(arbeidsforhold,mnd)).isTrue();
    }

    @Test
    public void datoSkalIkkeVaereInneforPeriode() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom = LocalDate.of(2017,9,1);
        LocalDate tom = LocalDate.of(2017,11,30);
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold().setFom(fom).setTom(tom);
        assertThat(erDatoInnenforPeriode(arbeidsforhold,mnd)).isFalse();
    }

    @Test
    public void skalHaArbeidsforholdPaaDato() {
        LocalDate mnd = LocalDate.of(2017,12,1);
        LocalDate fom1 = LocalDate.of(2017,10,1);
        LocalDate tom1 = LocalDate.of(2017,12,1);
        LocalDate fom2 = LocalDate.of(2017,12,1);
        LocalDate tom2 = LocalDate.of(2017,12,30);
        Arbeidsforhold arbeidsforhold1 = new Arbeidsforhold().setFom(fom1).setTom(tom1);
        Arbeidsforhold arbeidsforhold2 = new Arbeidsforhold().setFom(fom2).setTom(tom2);
        List<Arbeidsforhold> arbeidsforhold = asList(arbeidsforhold1, arbeidsforhold2);

        assertThat(harArbeidsforholdPaaDato(arbeidsforhold, mnd)).isTrue();
    }

    @Test
    public void skalIkkeHaArbeidsforholdPaaDato() {
        LocalDate mnd = LocalDate.of(2018,12,1);
        LocalDate fom1 = LocalDate.of(2017,10,1);
        LocalDate tom1 = LocalDate.of(2017,12,1);
        LocalDate fom2 = LocalDate.of(2017,12,1);
        LocalDate tom2 = LocalDate.of(2017,12,30);
        Arbeidsforhold arbeidsforhold1 = new Arbeidsforhold().setFom(fom1).setTom(tom1);
        Arbeidsforhold arbeidsforhold2 = new Arbeidsforhold().setFom(fom2).setTom(tom2);
        List<Arbeidsforhold> arbeidsforhold = asList(arbeidsforhold1, arbeidsforhold2);

        assertThat(harArbeidsforholdPaaDato(arbeidsforhold, mnd)).isFalse();
    }

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

        assertThat(oppfyllerKravOmArbeidserfaring(arbeidsforhold,dagensDato)).isTrue();
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

        assertThat(oppfyllerKravOmArbeidserfaring(arbeidsforhold,dagensDato)).isFalse();
    }

    @Test
    public void skalHenteSisteEllerPaagendeArbeidsforhold() {
        LocalDate fom0 = LocalDate.of(2017,11,1);
        LocalDate tom0 = null;
        LocalDate fom1 = LocalDate.of(2017,11,1);
        LocalDate tom1 = LocalDate.of(2017,11,30);
        LocalDate fom2 = LocalDate.of(2017,9,1);
        LocalDate tom2 = LocalDate.of(2017,9,30);
        LocalDate fom3 = LocalDate.of(2017,4,1);
        LocalDate tom3 = LocalDate.of(2017,4,30);

        Arbeidsforhold paagaaendeArbeidsforhold = new Arbeidsforhold().setFom(fom0).setTom(tom0);
        Arbeidsforhold sisteArbeidsforhold = new Arbeidsforhold().setFom(fom1).setTom(tom1);
        Arbeidsforhold nestSisteArbeidsforhold = new Arbeidsforhold().setFom(fom2).setTom(tom2);
        Arbeidsforhold eldreArbeidsforhold = new Arbeidsforhold().setFom(fom3).setTom(tom3);

        // Skal hente sistearbeidsforhold
        List<Arbeidsforhold> tilfeldigSortertListe = asList(eldreArbeidsforhold, sisteArbeidsforhold, nestSisteArbeidsforhold);
        assertThat(hentSisteArbeidsforhold(tilfeldigSortertListe)).isEqualTo(sisteArbeidsforhold);

        List<Arbeidsforhold> stigendeSortertListe = asList(eldreArbeidsforhold, nestSisteArbeidsforhold, sisteArbeidsforhold);
        assertThat(hentSisteArbeidsforhold(stigendeSortertListe)).isEqualTo(sisteArbeidsforhold);

        List<Arbeidsforhold> synkendeSortertListe = asList(sisteArbeidsforhold, nestSisteArbeidsforhold, eldreArbeidsforhold);
        assertThat(hentSisteArbeidsforhold(synkendeSortertListe)).isEqualTo(sisteArbeidsforhold);

        // Skal hente paagendeArbeidsforhold
        List<Arbeidsforhold> tilfeldigSortertListeMedPaagaaende = asList(eldreArbeidsforhold, paagaaendeArbeidsforhold, sisteArbeidsforhold, nestSisteArbeidsforhold);
        assertThat(hentSisteArbeidsforhold(tilfeldigSortertListeMedPaagaaende)).isEqualTo(paagaaendeArbeidsforhold);

        List<Arbeidsforhold> stigendeSortertListePaagaande = asList(eldreArbeidsforhold, nestSisteArbeidsforhold, sisteArbeidsforhold, paagaaendeArbeidsforhold);
        assertThat(hentSisteArbeidsforhold(stigendeSortertListePaagaande)).isEqualTo(paagaaendeArbeidsforhold);

        List<Arbeidsforhold> synkendeSortertListePaagaande = asList(paagaaendeArbeidsforhold, sisteArbeidsforhold, nestSisteArbeidsforhold, eldreArbeidsforhold);
        assertThat(hentSisteArbeidsforhold(synkendeSortertListePaagaande)).isEqualTo(paagaaendeArbeidsforhold);
    }

    @Test
    public void skalHenteLengsteAvPaagendeArbeidsforhold() {
        LocalDate fom3 = LocalDate.of(2017,1,1);
        LocalDate tom3 = null;
        LocalDate fom2 = LocalDate.of(2017,10,1);
        LocalDate tom2 = null;
        LocalDate fom1 = LocalDate.of(2017,11,1);
        LocalDate tom1 = null;

        Arbeidsforhold paagaaendeArbeidsforholdVarighet3 = new Arbeidsforhold().setFom(fom3).setTom(tom3);
        Arbeidsforhold paagaaendeArbeidsforholdVarighet2 = new Arbeidsforhold().setFom(fom2).setTom(tom2);
        Arbeidsforhold paagaaendeArbeidsforholdVarighet1 = new Arbeidsforhold().setFom(fom1).setTom(tom1);

        List<Arbeidsforhold> flerePaagendeArbeidsforhold =
                asList(paagaaendeArbeidsforholdVarighet2, paagaaendeArbeidsforholdVarighet1, paagaaendeArbeidsforholdVarighet3);
        assertThat(hentSisteArbeidsforhold(flerePaagendeArbeidsforhold)).isEqualTo(paagaaendeArbeidsforholdVarighet3);
    }

    @Test
    public void skalHenteLengsteAvSisteArbeidsforhold() {
        LocalDate fom3 = LocalDate.of(2017,1,1);
        LocalDate tom3 = LocalDate.of(2017,11,30);
        LocalDate fom2 = LocalDate.of(2017,10,1);
        LocalDate tom2 = LocalDate.of(2017,11,30);
        LocalDate fom1 = LocalDate.of(2017,11,1);
        LocalDate tom1 = LocalDate.of(2017,11,30);

        Arbeidsforhold sisteArbeidsforholdVarighet3 = new Arbeidsforhold().setFom(fom3).setTom(tom3);
        Arbeidsforhold sisteArbeidsforholdvarighet2 = new Arbeidsforhold().setFom(fom2).setTom(tom2);
        Arbeidsforhold sisteArbeidsforholdVarighet1 = new Arbeidsforhold().setFom(fom1).setTom(tom1);

        List<Arbeidsforhold> flereSisteArbeidsforhold =
                asList(sisteArbeidsforholdVarighet1, sisteArbeidsforholdvarighet2, sisteArbeidsforholdVarighet3);
        assertThat(hentSisteArbeidsforhold(flereSisteArbeidsforhold)).isEqualTo(sisteArbeidsforholdVarighet3);
    }

    @Test
    public void skalDefaultArbeidsforhold() {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold().setStyrk("utenstyrkkode");
        assertThat(hentSisteArbeidsforhold(Collections.emptyList())).isEqualTo(arbeidsforhold);
    }

}