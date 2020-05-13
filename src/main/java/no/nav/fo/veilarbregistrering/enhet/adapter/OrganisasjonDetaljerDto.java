package no.nav.fo.veilarbregistrering.enhet.adapter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrganisasjonDetaljerDto {

    private List<ForretningsAdresseDto> forretningsadresser;
    private List<PostadresseDto> postadresser;
}