package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import java.time.LocalDate;

public class ArbeidsforholdDto {
    private final String arbeidsgiverOrgnummer;
    private final String styrk;
    private final LocalDate fom;
    private final LocalDate tom;

    public ArbeidsforholdDto(String arbeidsgiverOrgnummer, String styrk, LocalDate fom, LocalDate tom) {
        this.arbeidsgiverOrgnummer = arbeidsgiverOrgnummer;
        this.styrk = styrk;
        this.fom = fom;
        this.tom = tom;
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
}
