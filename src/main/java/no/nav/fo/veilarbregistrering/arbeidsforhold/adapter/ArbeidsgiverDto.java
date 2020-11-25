package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import java.util.Objects;

class ArbeidsgiverDto {

    private String type;
    private String organisasjonsnummer;

    ArbeidsgiverDto() {
    }

    String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    void setOrganisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArbeidsgiverDto that = (ArbeidsgiverDto) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(organisasjonsnummer, that.organisasjonsnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, organisasjonsnummer);
    }
}
