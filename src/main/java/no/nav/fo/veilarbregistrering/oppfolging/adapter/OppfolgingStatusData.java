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

}