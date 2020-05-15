package no.nav.fo.veilarbregistrering.enhet.adapter;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class EnhetRestClientTest {

    private static final String OK_JSON = "/enhet/enhet.json";

    @Test
    public void skal_kunne_parse_json_til_organisasjonsdetaljer() {
        OrganisasjonDto organisasjonDto = EnhetRestClient.parse(toJson(OK_JSON));
        assertThat(organisasjonDto).isNotNull();
        assertThat(organisasjonDto.getOrganisasjonDetaljer()).isNotNull();
        assertThat(organisasjonDto.getOrganisasjonDetaljer().getForretningsadresser().get(0).getKommunenummer()).isEqualTo("0301");
    }

    private String toJson(String json_file) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(EnhetRestClient.class.getResource(json_file).toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
