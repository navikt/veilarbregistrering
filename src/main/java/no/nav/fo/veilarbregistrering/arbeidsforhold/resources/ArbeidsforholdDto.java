package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ArbeidsforholdDto {
    private String arbeidsgiverOrgnummer;
    private String styrk;
    private LocalDate fom;
    private LocalDate tom;

}
