package no.nav.fo.veilarbregistrering.bruker.krr;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class KrrClientTest {

    private static final String OK_JSON = "/krr/kontaktinformasjon.json";

    @Test
    public void skal_mappe_kontaktinfo_med_mobiltelefonnummer() {
        String json = toJson(OK_JSON);
        Foedselsnummer foedselsnummer = Foedselsnummer.of("23067844532");

        KrrKontaktinfoDto kontaktinfoDto = KrrClient.map(json, foedselsnummer);

        assertThat(kontaktinfoDto).isNotNull();
        assertThat(kontaktinfoDto.getMobiltelefonnummer()).isEqualTo("11111111");
    }

    private String toJson(String json_file) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(KrrClient.class.getResource(json_file).toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
