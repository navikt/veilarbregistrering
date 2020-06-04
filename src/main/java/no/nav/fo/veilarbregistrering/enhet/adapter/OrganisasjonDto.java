package no.nav.fo.veilarbregistrering.enhet.adapter;

public class OrganisasjonDto {

    private final String organisasjonsnummer;
    private final OrganisasjonDetaljerDto organisasjonDetaljer;

    public OrganisasjonDto(String organisasjonsnummer, OrganisasjonDetaljerDto organisasjonDetaljer) {
        this.organisasjonsnummer = organisasjonsnummer;
        this.organisasjonDetaljer = organisasjonDetaljer;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public OrganisasjonDetaljerDto getOrganisasjonDetaljer() {
        return organisasjonDetaljer;
    }
}