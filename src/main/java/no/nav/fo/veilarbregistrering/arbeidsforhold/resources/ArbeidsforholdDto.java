package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import java.time.LocalDate;

public class ArbeidsforholdDto {
    private String arbeidsgiverOrgnummer;
    private String styrk;
    private LocalDate fom;
    private LocalDate tom;

    public ArbeidsforholdDto() {
    }

    public String getArbeidsgiverOrgnummer() {
        return this.arbeidsgiverOrgnummer;
    }

    public String getStyrk() {
        return this.styrk;
    }

    public LocalDate getFom() {
        return this.fom;
    }

    public LocalDate getTom() {
        return this.tom;
    }

    public ArbeidsforholdDto setArbeidsgiverOrgnummer(String arbeidsgiverOrgnummer) {
        this.arbeidsgiverOrgnummer = arbeidsgiverOrgnummer;
        return this;
    }

    public ArbeidsforholdDto setStyrk(String styrk) {
        this.styrk = styrk;
        return this;
    }

    public ArbeidsforholdDto setFom(LocalDate fom) {
        this.fom = fom;
        return this;
    }

    public ArbeidsforholdDto setTom(LocalDate tom) {
        this.tom = tom;
        return this;
    }
}
