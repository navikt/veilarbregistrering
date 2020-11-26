package no.nav.fo.veilarbregistrering.sykemelding;

public class SykmeldtInfoData {
    public String maksDato;
    public boolean erArbeidsrettetOppfolgingSykmeldtInngangAktiv;

    public SykmeldtInfoData(String maksDato, boolean erArbeidsrettetOppfolgingSykmeldtInngangAktiv) {
        this.maksDato = maksDato;
        this.erArbeidsrettetOppfolgingSykmeldtInngangAktiv = erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
    }

    public SykmeldtInfoData() {
    }

    public String getMaksDato() {
        return this.maksDato;
    }

    public boolean isErArbeidsrettetOppfolgingSykmeldtInngangAktiv() {
        return this.erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
    }

    public void setMaksDato(String maksDato) {
        this.maksDato = maksDato;
    }

    public void setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(boolean erArbeidsrettetOppfolgingSykmeldtInngangAktiv) {
        this.erArbeidsrettetOppfolgingSykmeldtInngangAktiv = erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof SykmeldtInfoData)) return false;
        final SykmeldtInfoData other = (SykmeldtInfoData) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$maksDato = this.getMaksDato();
        final Object other$maksDato = other.getMaksDato();
        if (this$maksDato == null ? other$maksDato != null : !this$maksDato.equals(other$maksDato)) return false;
        if (this.isErArbeidsrettetOppfolgingSykmeldtInngangAktiv() != other.isErArbeidsrettetOppfolgingSykmeldtInngangAktiv())
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SykmeldtInfoData;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $maksDato = this.getMaksDato();
        result = result * PRIME + ($maksDato == null ? 43 : $maksDato.hashCode());
        result = result * PRIME + (this.isErArbeidsrettetOppfolgingSykmeldtInngangAktiv() ? 79 : 97);
        return result;
    }

    public SykmeldtInfoData withMaksDato(String maksDato) {
        return this.maksDato == maksDato ? this : new SykmeldtInfoData(maksDato, this.erArbeidsrettetOppfolgingSykmeldtInngangAktiv);
    }

    public SykmeldtInfoData withErArbeidsrettetOppfolgingSykmeldtInngangAktiv(boolean erArbeidsrettetOppfolgingSykmeldtInngangAktiv) {
        return this.erArbeidsrettetOppfolgingSykmeldtInngangAktiv == erArbeidsrettetOppfolgingSykmeldtInngangAktiv ? this : new SykmeldtInfoData(this.maksDato, erArbeidsrettetOppfolgingSykmeldtInngangAktiv);
    }

    public String toString() {
        return "SykmeldtInfoData(maksDato=" + this.getMaksDato() + ", erArbeidsrettetOppfolgingSykmeldtInngangAktiv=" + this.isErArbeidsrettetOppfolgingSykmeldtInngangAktiv() + ")";
    }
}