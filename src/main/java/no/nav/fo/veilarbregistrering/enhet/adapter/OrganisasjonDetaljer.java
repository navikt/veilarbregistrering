package no.nav.fo.veilarbregistrering.enhet.adapter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrganisasjonDetaljer {

    private List<ForretningsAdresse> forretningsadresser;
    private List<Postadresse> postadresser;
}