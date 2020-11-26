package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.time.LocalDate;

public class ArbeidsforholdTestdataBuilder {

    public static Arbeidsforhold paagaaende() {
        LocalDate fom = LocalDate.of(2017,11,1);
        LocalDate tom = null;
        return new Arbeidsforhold("555555555", null, fom, tom);
    }

    public static Arbeidsforhold siste() {
        LocalDate fom = LocalDate.of(2017,11,1);
        LocalDate tom = LocalDate.of(2017,11,30);
        return new Arbeidsforhold("123456789", null, fom, tom);
    }

    public static Arbeidsforhold nestSiste() {
        LocalDate fom = LocalDate.of(2017,9,1);
        LocalDate tom = LocalDate.of(2017,9,30);
        return new Arbeidsforhold("987654321", null, fom, tom);
    }

    public static Arbeidsforhold eldre() {
        LocalDate fom = LocalDate.of(2017,4,1);
        LocalDate tom = LocalDate.of(2017,4,30);
        return new Arbeidsforhold(null, null, fom, tom);
    }
}
