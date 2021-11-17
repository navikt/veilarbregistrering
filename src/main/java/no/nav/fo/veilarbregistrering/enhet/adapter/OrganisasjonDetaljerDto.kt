package no.nav.fo.veilarbregistrering.enhet.adapter;

import java.util.List;

import static java.util.Collections.emptyList;

public class OrganisasjonDetaljerDto {

    private final List<ForretningsAdresseDto> forretningsadresser;
    private final List<PostadresseDto> postadresser;

    public OrganisasjonDetaljerDto(List<ForretningsAdresseDto> forretningsadresser, List<PostadresseDto> postadresser) {
        this.forretningsadresser = forretningsadresser;
        this.postadresser = postadresser;
    }

    public List<ForretningsAdresseDto> getForretningsadresser() {
        return forretningsadresser != null ? forretningsadresser : emptyList();
    }

    public List<PostadresseDto> getPostadresser() {
        return postadresser != null ? postadresser : emptyList();
    }
}