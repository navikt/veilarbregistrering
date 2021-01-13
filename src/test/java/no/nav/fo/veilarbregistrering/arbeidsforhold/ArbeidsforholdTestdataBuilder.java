package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.time.LocalDate;

public class ArbeidsforholdTestdataBuilder {

    private LocalDate fom;
    private LocalDate tom;
    private String organisasjonsnummer;
    private String styrk;
    private String navArbeidsforholdId;

    public static Arbeidsforhold tidligereArbeidsforhold() {
        return new ArbeidsforholdTestdataBuilder()
                .periode(
                        LocalDate.of(2012, 3, 1),
                        LocalDate.of(2016, 3, 31))
                .build();
    }

    public static Arbeidsforhold åpentArbeidsforhold() {
        return new ArbeidsforholdTestdataBuilder()
                .periode(
                        LocalDate.of(2016, 4, 1),
                        null)
                .build();
    }

    public static Arbeidsforhold paagaaende() {
        return new ArbeidsforholdTestdataBuilder()
                .organisasjonsnummer("555555555")
                .periode(LocalDate.of(2017, 11, 1), null)
                .build();
    }

    public static Arbeidsforhold siste() {
        return new ArbeidsforholdTestdataBuilder()
                .organisasjonsnummer("123456789")
                .periode(
                        LocalDate.of(2017, 11, 1),
                        LocalDate.of(2017, 11, 30))
                .build();
    }

    public static Arbeidsforhold nestSiste() {
        return new ArbeidsforholdTestdataBuilder()
                .organisasjonsnummer("987654321")
                .periode(
                        LocalDate.of(2017, 9, 1),
                        LocalDate.of(2017, 9, 30))
                .build();
    }

    public static Arbeidsforhold eldre() {
        return new ArbeidsforholdTestdataBuilder()
                .periode(
                        LocalDate.of(2017, 4, 1),
                        LocalDate.of(2017, 4, 30))
                .build();
    }

    public static Arbeidsforhold medDato(LocalDate fom, LocalDate tom) {
        return new ArbeidsforholdTestdataBuilder().periode(fom, tom).build();
    }

    public ArbeidsforholdTestdataBuilder periode(LocalDate fom, LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
        return this;
    }

    public ArbeidsforholdTestdataBuilder organisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
        return this;
    }

    public ArbeidsforholdTestdataBuilder styrk(String styrk) {
        this.styrk = styrk;
        return this;
    }

    public ArbeidsforholdTestdataBuilder navArbeidsforholdId(String navArbeidsforholdId) {
        this.navArbeidsforholdId = navArbeidsforholdId;
        return this;
    }

    public Arbeidsforhold build() {
        return new Arbeidsforhold(organisasjonsnummer, styrk, fom, tom, navArbeidsforholdId);
    }
}