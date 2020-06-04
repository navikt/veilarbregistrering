package no.nav.fo.veilarbregistrering.enhet.adapter;

import java.util.List;

import static java.util.Collections.emptyList;

public class OrganisasjonDetaljerDto {

    private List<ForretningsAdresseDto> forretningsadresser;
    private List<PostadresseDto> postadresser;

    OrganisasjonDetaljerDto() {
        //default constructor for Gson
    }

    public OrganisasjonDetaljerDto(List<ForretningsAdresseDto> forretningsadresser, List<PostadresseDto> postadresser) {
        this.forretningsadresser = forretningsadresser;
        this.postadresser = postadresser;
    }

    public void setForretningsadresser(List<ForretningsAdresseDto> forretningsadresser) {
        this.forretningsadresser = forretningsadresser;
    }

    public List<ForretningsAdresseDto> getForretningsadresser() {
        return forretningsadresser != null ? forretningsadresser : emptyList();
    }

    public void setPostadresser(List<PostadresseDto> postadresser) {
        this.postadresser = postadresser != null ? postadresser : emptyList();
    }

    public List<PostadresseDto> getPostadresser() {
        return postadresser != null ? postadresser : emptyList();
    }
}