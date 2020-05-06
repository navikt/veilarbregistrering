package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.time.LocalDate;

public class ArbeidsforholdTestdataBuilder {

    public static Arbeidsforhold paagaaende() {
        LocalDate fom0 = LocalDate.of(2017,11,1);
        LocalDate tom0 = null;
        return new Arbeidsforhold().setFom(fom0).setTom(tom0);
    }

    public static Arbeidsforhold siste() {
        LocalDate fom1 = LocalDate.of(2017,11,1);
        LocalDate tom1 = LocalDate.of(2017,11,30);
        return new Arbeidsforhold().setFom(fom1).setTom(tom1);
    }

    public static Arbeidsforhold nestSiste() {
        LocalDate fom2 = LocalDate.of(2017,9,1);
        LocalDate tom2 = LocalDate.of(2017,9,30);
        return new Arbeidsforhold().setFom(fom2).setTom(tom2);
    }

    public static Arbeidsforhold eldre() {
        LocalDate fom3 = LocalDate.of(2017,4,1);
        LocalDate tom3 = LocalDate.of(2017,4,30);
        return new Arbeidsforhold().setFom(fom3).setTom(tom3);
    }
}
