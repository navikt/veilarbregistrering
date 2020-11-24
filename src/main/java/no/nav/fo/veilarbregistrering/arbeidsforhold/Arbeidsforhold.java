package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Arbeidsforhold {
    private String arbeidsgiverOrgnummer;
    private String styrk;
    private LocalDate fom;
    private LocalDate tom;

    public Arbeidsforhold() {
    }

    static Arbeidsforhold utenStyrkkode() {
        return new Arbeidsforhold().setStyrk("utenstyrkkode");
    }

    boolean erDatoInnenforPeriode(LocalDate innevaerendeMnd) {
        return innevaerendeMnd.isAfter(fom.minusDays(1)) &&
                (Objects.isNull(tom) || innevaerendeMnd.isBefore(tom.plusDays(1)));
    }

    public Optional<Organisasjonsnummer> getOrganisasjonsnummer() {
        return arbeidsgiverOrgnummer != null
                ? Optional.of(Organisasjonsnummer.of(arbeidsgiverOrgnummer))
                : Optional.empty();
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

    public Arbeidsforhold setArbeidsgiverOrgnummer(String arbeidsgiverOrgnummer) {
        this.arbeidsgiverOrgnummer = arbeidsgiverOrgnummer;
        return this;
    }

    public Arbeidsforhold setStyrk(String styrk) {
        this.styrk = styrk;
        return this;
    }

    public Arbeidsforhold setFom(LocalDate fom) {
        this.fom = fom;
        return this;
    }

    public Arbeidsforhold setTom(LocalDate tom) {
        this.tom = tom;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Arbeidsforhold)) return false;
        final Arbeidsforhold other = (Arbeidsforhold) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$arbeidsgiverOrgnummer = this.getArbeidsgiverOrgnummer();
        final Object other$arbeidsgiverOrgnummer = other.getArbeidsgiverOrgnummer();
        if (this$arbeidsgiverOrgnummer == null ? other$arbeidsgiverOrgnummer != null : !this$arbeidsgiverOrgnummer.equals(other$arbeidsgiverOrgnummer))
            return false;
        final Object this$styrk = this.getStyrk();
        final Object other$styrk = other.getStyrk();
        if (this$styrk == null ? other$styrk != null : !this$styrk.equals(other$styrk)) return false;
        final Object this$fom = this.getFom();
        final Object other$fom = other.getFom();
        if (this$fom == null ? other$fom != null : !this$fom.equals(other$fom)) return false;
        final Object this$tom = this.getTom();
        final Object other$tom = other.getTom();
        if (this$tom == null ? other$tom != null : !this$tom.equals(other$tom)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Arbeidsforhold;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $arbeidsgiverOrgnummer = this.getArbeidsgiverOrgnummer();
        result = result * PRIME + ($arbeidsgiverOrgnummer == null ? 43 : $arbeidsgiverOrgnummer.hashCode());
        final Object $styrk = this.getStyrk();
        result = result * PRIME + ($styrk == null ? 43 : $styrk.hashCode());
        final Object $fom = this.getFom();
        result = result * PRIME + ($fom == null ? 43 : $fom.hashCode());
        final Object $tom = this.getTom();
        result = result * PRIME + ($tom == null ? 43 : $tom.hashCode());
        return result;
    }

    public String toString() {
        return "Arbeidsforhold(arbeidsgiverOrgnummer=" + this.getArbeidsgiverOrgnummer() + ", styrk=" + this.getStyrk() + ", fom=" + this.getFom() + ", tom=" + this.getTom() + ")";
    }
}
