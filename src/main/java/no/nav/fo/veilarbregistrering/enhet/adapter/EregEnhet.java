package no.nav.fo.veilarbregistrering.enhet.adapter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EregEnhet {
    private String organisasjonsnummer;
    private OrganisasjonDetaljer organisasjonDetaljer;
}