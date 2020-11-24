package no.nav.fo.veilarbregistrering.oppfolging.adapter;

public class OppfolgingStatusData {
    public boolean underOppfolging;
    public Boolean kanReaktiveres;
    public Boolean erSykmeldtMedArbeidsgiver;
    public String formidlingsgruppe;
    public String servicegruppe;
    public String rettighetsgruppe;

    @Deprecated
    public Boolean erIkkeArbeidssokerUtenOppfolging;

    public OppfolgingStatusData(boolean underOppfolging, Boolean kanReaktiveres, Boolean erSykmeldtMedArbeidsgiver, String formidlingsgruppe, String servicegruppe, String rettighetsgruppe, Boolean erIkkeArbeidssokerUtenOppfolging) {
        this.underOppfolging = underOppfolging;
        this.kanReaktiveres = kanReaktiveres;
        this.erSykmeldtMedArbeidsgiver = erSykmeldtMedArbeidsgiver;
        this.formidlingsgruppe = formidlingsgruppe;
        this.servicegruppe = servicegruppe;
        this.rettighetsgruppe = rettighetsgruppe;
        this.erIkkeArbeidssokerUtenOppfolging = erIkkeArbeidssokerUtenOppfolging;
    }

    public OppfolgingStatusData() {
    }

    public boolean isUnderOppfolging() {
        return this.underOppfolging;
    }

    public Boolean getKanReaktiveres() {
        return this.kanReaktiveres;
    }

    public Boolean getErSykmeldtMedArbeidsgiver() {
        return this.erSykmeldtMedArbeidsgiver;
    }

    public String getFormidlingsgruppe() {
        return this.formidlingsgruppe;
    }

    public String getServicegruppe() {
        return this.servicegruppe;
    }

    public String getRettighetsgruppe() {
        return this.rettighetsgruppe;
    }

    @Deprecated
    public Boolean getErIkkeArbeidssokerUtenOppfolging() {
        return this.erIkkeArbeidssokerUtenOppfolging;
    }

    public void setUnderOppfolging(boolean underOppfolging) {
        this.underOppfolging = underOppfolging;
    }

    public void setKanReaktiveres(Boolean kanReaktiveres) {
        this.kanReaktiveres = kanReaktiveres;
    }

    public void setErSykmeldtMedArbeidsgiver(Boolean erSykmeldtMedArbeidsgiver) {
        this.erSykmeldtMedArbeidsgiver = erSykmeldtMedArbeidsgiver;
    }

    public void setFormidlingsgruppe(String formidlingsgruppe) {
        this.formidlingsgruppe = formidlingsgruppe;
    }

    public void setServicegruppe(String servicegruppe) {
        this.servicegruppe = servicegruppe;
    }

    public void setRettighetsgruppe(String rettighetsgruppe) {
        this.rettighetsgruppe = rettighetsgruppe;
    }

    @Deprecated
    public void setErIkkeArbeidssokerUtenOppfolging(Boolean erIkkeArbeidssokerUtenOppfolging) {
        this.erIkkeArbeidssokerUtenOppfolging = erIkkeArbeidssokerUtenOppfolging;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OppfolgingStatusData)) return false;
        final OppfolgingStatusData other = (OppfolgingStatusData) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isUnderOppfolging() != other.isUnderOppfolging()) return false;
        final Object this$kanReaktiveres = this.getKanReaktiveres();
        final Object other$kanReaktiveres = other.getKanReaktiveres();
        if (this$kanReaktiveres == null ? other$kanReaktiveres != null : !this$kanReaktiveres.equals(other$kanReaktiveres))
            return false;
        final Object this$erSykmeldtMedArbeidsgiver = this.getErSykmeldtMedArbeidsgiver();
        final Object other$erSykmeldtMedArbeidsgiver = other.getErSykmeldtMedArbeidsgiver();
        if (this$erSykmeldtMedArbeidsgiver == null ? other$erSykmeldtMedArbeidsgiver != null : !this$erSykmeldtMedArbeidsgiver.equals(other$erSykmeldtMedArbeidsgiver))
            return false;
        final Object this$formidlingsgruppe = this.getFormidlingsgruppe();
        final Object other$formidlingsgruppe = other.getFormidlingsgruppe();
        if (this$formidlingsgruppe == null ? other$formidlingsgruppe != null : !this$formidlingsgruppe.equals(other$formidlingsgruppe))
            return false;
        final Object this$servicegruppe = this.getServicegruppe();
        final Object other$servicegruppe = other.getServicegruppe();
        if (this$servicegruppe == null ? other$servicegruppe != null : !this$servicegruppe.equals(other$servicegruppe))
            return false;
        final Object this$rettighetsgruppe = this.getRettighetsgruppe();
        final Object other$rettighetsgruppe = other.getRettighetsgruppe();
        if (this$rettighetsgruppe == null ? other$rettighetsgruppe != null : !this$rettighetsgruppe.equals(other$rettighetsgruppe))
            return false;
        final Object this$erIkkeArbeidssokerUtenOppfolging = this.getErIkkeArbeidssokerUtenOppfolging();
        final Object other$erIkkeArbeidssokerUtenOppfolging = other.getErIkkeArbeidssokerUtenOppfolging();
        if (this$erIkkeArbeidssokerUtenOppfolging == null ? other$erIkkeArbeidssokerUtenOppfolging != null : !this$erIkkeArbeidssokerUtenOppfolging.equals(other$erIkkeArbeidssokerUtenOppfolging))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OppfolgingStatusData;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isUnderOppfolging() ? 79 : 97);
        final Object $kanReaktiveres = this.getKanReaktiveres();
        result = result * PRIME + ($kanReaktiveres == null ? 43 : $kanReaktiveres.hashCode());
        final Object $erSykmeldtMedArbeidsgiver = this.getErSykmeldtMedArbeidsgiver();
        result = result * PRIME + ($erSykmeldtMedArbeidsgiver == null ? 43 : $erSykmeldtMedArbeidsgiver.hashCode());
        final Object $formidlingsgruppe = this.getFormidlingsgruppe();
        result = result * PRIME + ($formidlingsgruppe == null ? 43 : $formidlingsgruppe.hashCode());
        final Object $servicegruppe = this.getServicegruppe();
        result = result * PRIME + ($servicegruppe == null ? 43 : $servicegruppe.hashCode());
        final Object $rettighetsgruppe = this.getRettighetsgruppe();
        result = result * PRIME + ($rettighetsgruppe == null ? 43 : $rettighetsgruppe.hashCode());
        final Object $erIkkeArbeidssokerUtenOppfolging = this.getErIkkeArbeidssokerUtenOppfolging();
        result = result * PRIME + ($erIkkeArbeidssokerUtenOppfolging == null ? 43 : $erIkkeArbeidssokerUtenOppfolging.hashCode());
        return result;
    }

    public OppfolgingStatusData withUnderOppfolging(boolean underOppfolging) {
        return this.underOppfolging == underOppfolging ? this : new OppfolgingStatusData(underOppfolging, this.kanReaktiveres, this.erSykmeldtMedArbeidsgiver, this.formidlingsgruppe, this.servicegruppe, this.rettighetsgruppe, this.erIkkeArbeidssokerUtenOppfolging);
    }

    public OppfolgingStatusData withKanReaktiveres(Boolean kanReaktiveres) {
        return this.kanReaktiveres == kanReaktiveres ? this : new OppfolgingStatusData(this.underOppfolging, kanReaktiveres, this.erSykmeldtMedArbeidsgiver, this.formidlingsgruppe, this.servicegruppe, this.rettighetsgruppe, this.erIkkeArbeidssokerUtenOppfolging);
    }

    public OppfolgingStatusData withErSykmeldtMedArbeidsgiver(Boolean erSykmeldtMedArbeidsgiver) {
        return this.erSykmeldtMedArbeidsgiver == erSykmeldtMedArbeidsgiver ? this : new OppfolgingStatusData(this.underOppfolging, this.kanReaktiveres, erSykmeldtMedArbeidsgiver, this.formidlingsgruppe, this.servicegruppe, this.rettighetsgruppe, this.erIkkeArbeidssokerUtenOppfolging);
    }

    public OppfolgingStatusData withFormidlingsgruppe(String formidlingsgruppe) {
        return this.formidlingsgruppe == formidlingsgruppe ? this : new OppfolgingStatusData(this.underOppfolging, this.kanReaktiveres, this.erSykmeldtMedArbeidsgiver, formidlingsgruppe, this.servicegruppe, this.rettighetsgruppe, this.erIkkeArbeidssokerUtenOppfolging);
    }

    public OppfolgingStatusData withServicegruppe(String servicegruppe) {
        return this.servicegruppe == servicegruppe ? this : new OppfolgingStatusData(this.underOppfolging, this.kanReaktiveres, this.erSykmeldtMedArbeidsgiver, this.formidlingsgruppe, servicegruppe, this.rettighetsgruppe, this.erIkkeArbeidssokerUtenOppfolging);
    }

    public OppfolgingStatusData withRettighetsgruppe(String rettighetsgruppe) {
        return this.rettighetsgruppe == rettighetsgruppe ? this : new OppfolgingStatusData(this.underOppfolging, this.kanReaktiveres, this.erSykmeldtMedArbeidsgiver, this.formidlingsgruppe, this.servicegruppe, rettighetsgruppe, this.erIkkeArbeidssokerUtenOppfolging);
    }

    public OppfolgingStatusData withErIkkeArbeidssokerUtenOppfolging(@Deprecated Boolean erIkkeArbeidssokerUtenOppfolging) {
        return this.erIkkeArbeidssokerUtenOppfolging == erIkkeArbeidssokerUtenOppfolging ? this : new OppfolgingStatusData(this.underOppfolging, this.kanReaktiveres, this.erSykmeldtMedArbeidsgiver, this.formidlingsgruppe, this.servicegruppe, this.rettighetsgruppe, erIkkeArbeidssokerUtenOppfolging);
    }

    public String toString() {
        return "OppfolgingStatusData(underOppfolging=" + this.isUnderOppfolging() + ", kanReaktiveres=" + this.getKanReaktiveres() + ", erSykmeldtMedArbeidsgiver=" + this.getErSykmeldtMedArbeidsgiver() + ", formidlingsgruppe=" + this.getFormidlingsgruppe() + ", servicegruppe=" + this.getServicegruppe() + ", rettighetsgruppe=" + this.getRettighetsgruppe() + ", erIkkeArbeidssokerUtenOppfolging=" + this.getErIkkeArbeidssokerUtenOppfolging() + ")";
    }
}