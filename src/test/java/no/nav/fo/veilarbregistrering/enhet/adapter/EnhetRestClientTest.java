package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.FileToJson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnhetRestClientTest {

    private static final String OK_JSON = "/enhet/enhet.json";
    private static final String MANGLER_FORRETNINGSADRESSE_JSON = "/enhet/enhetUtenForretningsadresse.json";

    @Test
    public void skal_kunne_parse_json_til_organisasjonsdetaljer() {
        OrganisasjonDto organisasjonDto = EnhetRestClient.parse(FileToJson.toJson(OK_JSON));
        assertThat(organisasjonDto).isNotNull();
        assertThat(organisasjonDto.getOrganisasjonDetaljer()).isNotNull();
        assertThat(organisasjonDto.getOrganisasjonDetaljer().getForretningsadresser().get(0).getKommunenummer()).isEqualTo("0301");
    }

    @Test
    public void skal_kunne_parse_json_uten_forretningsadresse_til_organisasjonsdetaljer() {
        OrganisasjonDto organisasjonDto = EnhetRestClient.parse(FileToJson.toJson(MANGLER_FORRETNINGSADRESSE_JSON));
        assertThat(organisasjonDto).isNotNull();
        assertThat(organisasjonDto.getOrganisasjonDetaljer()).isNotNull();
        assertThat(organisasjonDto.getOrganisasjonDetaljer().getForretningsadresser()).isEmpty();
    }

}
