package no.nav.fo.veilarbregistrering.arbeidsforhold;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class Arbeidsforhold {
    private String arbeidsgiverOrgnummer;
    private String styrk;
    private LocalDate fom;
    private LocalDate tom;

    static Arbeidsforhold utenStyrkkode() {
        return new Arbeidsforhold().setStyrk("utenstyrkkode");
    }

    boolean erDatoInnenforPeriode(LocalDate innevaerendeMnd) {
        return innevaerendeMnd.isAfter(fom.minusDays(1)) &&
                (Objects.isNull(tom) || innevaerendeMnd.isBefore(tom.plusDays(1)));
    }

    public Organisasjonsnummer getOrganisasjonsnummer() {
        return Organisasjonsnummer.of(arbeidsgiverOrgnummer);
    }
}
