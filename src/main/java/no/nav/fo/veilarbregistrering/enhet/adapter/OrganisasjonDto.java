package no.nav.fo.veilarbregistrering.enhet.adapter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrganisasjonDto {

    private String organisasjonsnummer;
    private OrganisasjonDetaljerDto organisasjonDetaljer;
}