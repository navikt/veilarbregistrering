package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType;

public class StartRegistreringStatusDto {
    private String maksDato;
    private boolean underOppfolging;
    private boolean erSykmeldtMedArbeidsgiver;
    private Boolean jobbetSeksAvTolvSisteManeder;
    private RegistreringType registreringType;
    private String formidlingsgruppe;
    private String servicegruppe;
    private String rettighetsgruppe;
    private String geografiskTilknytning;
    private int alder;

    public StartRegistreringStatusDto() {
    }

    public String getMaksDato() {
        return this.maksDato;
    }

    public boolean isUnderOppfolging() {
        return this.underOppfolging;
    }

    public boolean isErSykmeldtMedArbeidsgiver() {
        return this.erSykmeldtMedArbeidsgiver;
    }

    public Boolean getJobbetSeksAvTolvSisteManeder() {
        return this.jobbetSeksAvTolvSisteManeder;
    }

    public RegistreringType getRegistreringType() {
        return this.registreringType;
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

    public String getGeografiskTilknytning() {
        return this.geografiskTilknytning;
    }

    public int getAlder() {
        return this.alder;
    }

    public StartRegistreringStatusDto setMaksDato(String maksDato) {
        this.maksDato = maksDato;
        return this;
    }

    public StartRegistreringStatusDto setUnderOppfolging(boolean underOppfolging) {
        this.underOppfolging = underOppfolging;
        return this;
    }

    public StartRegistreringStatusDto setErSykmeldtMedArbeidsgiver(boolean erSykmeldtMedArbeidsgiver) {
        this.erSykmeldtMedArbeidsgiver = erSykmeldtMedArbeidsgiver;
        return this;
    }

    public StartRegistreringStatusDto setJobbetSeksAvTolvSisteManeder(Boolean jobbetSeksAvTolvSisteManeder) {
        this.jobbetSeksAvTolvSisteManeder = jobbetSeksAvTolvSisteManeder;
        return this;
    }

    public StartRegistreringStatusDto setRegistreringType(RegistreringType registreringType) {
        this.registreringType = registreringType;
        return this;
    }

    public StartRegistreringStatusDto setFormidlingsgruppe(String formidlingsgruppe) {
        this.formidlingsgruppe = formidlingsgruppe;
        return this;
    }

    public StartRegistreringStatusDto setServicegruppe(String servicegruppe) {
        this.servicegruppe = servicegruppe;
        return this;
    }

    public StartRegistreringStatusDto setRettighetsgruppe(String rettighetsgruppe) {
        this.rettighetsgruppe = rettighetsgruppe;
        return this;
    }

    public StartRegistreringStatusDto setGeografiskTilknytning(String geografiskTilknytning) {
        this.geografiskTilknytning = geografiskTilknytning;
        return this;
    }

    public StartRegistreringStatusDto setAlder(int alder) {
        this.alder = alder;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof StartRegistreringStatusDto))
            return false;
        final StartRegistreringStatusDto other = (StartRegistreringStatusDto) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$maksDato = this.getMaksDato();
        final Object other$maksDato = other.getMaksDato();
        if (this$maksDato == null ? other$maksDato != null : !this$maksDato.equals(other$maksDato)) return false;
        if (this.isUnderOppfolging() != other.isUnderOppfolging()) return false;
        if (this.isErSykmeldtMedArbeidsgiver() != other.isErSykmeldtMedArbeidsgiver()) return false;
        final Object this$jobbetSeksAvTolvSisteManeder = this.getJobbetSeksAvTolvSisteManeder();
        final Object other$jobbetSeksAvTolvSisteManeder = other.getJobbetSeksAvTolvSisteManeder();
        if (this$jobbetSeksAvTolvSisteManeder == null ? other$jobbetSeksAvTolvSisteManeder != null : !this$jobbetSeksAvTolvSisteManeder.equals(other$jobbetSeksAvTolvSisteManeder))
            return false;
        final Object this$registreringType = this.getRegistreringType();
        final Object other$registreringType = other.getRegistreringType();
        if (this$registreringType == null ? other$registreringType != null : !this$registreringType.equals(other$registreringType))
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
        final Object this$geografiskTilknytning = this.getGeografiskTilknytning();
        final Object other$geografiskTilknytning = other.getGeografiskTilknytning();
        if (this$geografiskTilknytning == null ? other$geografiskTilknytning != null : !this$geografiskTilknytning.equals(other$geografiskTilknytning))
            return false;
        if (this.getAlder() != other.getAlder()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof StartRegistreringStatusDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $maksDato = this.getMaksDato();
        result = result * PRIME + ($maksDato == null ? 43 : $maksDato.hashCode());
        result = result * PRIME + (this.isUnderOppfolging() ? 79 : 97);
        result = result * PRIME + (this.isErSykmeldtMedArbeidsgiver() ? 79 : 97);
        final Object $jobbetSeksAvTolvSisteManeder = this.getJobbetSeksAvTolvSisteManeder();
        result = result * PRIME + ($jobbetSeksAvTolvSisteManeder == null ? 43 : $jobbetSeksAvTolvSisteManeder.hashCode());
        final Object $registreringType = this.getRegistreringType();
        result = result * PRIME + ($registreringType == null ? 43 : $registreringType.hashCode());
        final Object $formidlingsgruppe = this.getFormidlingsgruppe();
        result = result * PRIME + ($formidlingsgruppe == null ? 43 : $formidlingsgruppe.hashCode());
        final Object $servicegruppe = this.getServicegruppe();
        result = result * PRIME + ($servicegruppe == null ? 43 : $servicegruppe.hashCode());
        final Object $rettighetsgruppe = this.getRettighetsgruppe();
        result = result * PRIME + ($rettighetsgruppe == null ? 43 : $rettighetsgruppe.hashCode());
        final Object $geografiskTilknytning = this.getGeografiskTilknytning();
        result = result * PRIME + ($geografiskTilknytning == null ? 43 : $geografiskTilknytning.hashCode());
        result = result * PRIME + this.getAlder();
        return result;
    }

    public String toString() {
        return "StartRegistreringStatusDto(maksDato=" + this.getMaksDato() + ", underOppfolging=" + this.isUnderOppfolging() + ", erSykmeldtMedArbeidsgiver=" + this.isErSykmeldtMedArbeidsgiver() + ", jobbetSeksAvTolvSisteManeder=" + this.getJobbetSeksAvTolvSisteManeder() + ", registreringType=" + this.getRegistreringType() + ", formidlingsgruppe=" + this.getFormidlingsgruppe() + ", servicegruppe=" + this.getServicegruppe() + ", rettighetsgruppe=" + this.getRettighetsgruppe() + ", geografiskTilknytning=" + this.getGeografiskTilknytning() + ", alder=" + this.getAlder() + ")";
    }
}
