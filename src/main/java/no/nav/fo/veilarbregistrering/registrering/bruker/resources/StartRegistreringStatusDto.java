package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

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

    public String toString() {
        return "StartRegistreringStatusDto(maksDato=" + this.getMaksDato() + ", underOppfolging=" + this.isUnderOppfolging() + ", erSykmeldtMedArbeidsgiver=" + this.isErSykmeldtMedArbeidsgiver() + ", jobbetSeksAvTolvSisteManeder=" + this.getJobbetSeksAvTolvSisteManeder() + ", registreringType=" + this.getRegistreringType() + ", formidlingsgruppe=" + this.getFormidlingsgruppe() + ", servicegruppe=" + this.getServicegruppe() + ", rettighetsgruppe=" + this.getRettighetsgruppe() + ", geografiskTilknytning=" + this.getGeografiskTilknytning() + ", alder=" + this.getAlder() + ")";
    }
}
