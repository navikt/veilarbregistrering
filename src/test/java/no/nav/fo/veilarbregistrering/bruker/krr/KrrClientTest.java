package no.nav.fo.veilarbregistrering.bruker.krr;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KrrClientTest {

    private static final String OK_JSON = "/krr/hentKontaktinformasjonOk.json";
    private static final String FEIL_JSON = "/krr/hentKontaktinformasjonError.json";

    @Test
    public void skal_mappe_kontaktinfo_med_mobiltelefonnummer() {
        String json = toJson(OK_JSON);
        Foedselsnummer foedselsnummer = Foedselsnummer.of("23067844532");

        KrrKontaktinfoDto kontaktinfoDto = KrrClient.parse(json, foedselsnummer);

        assertThat(kontaktinfoDto).isNotNull();
        assertThat(kontaktinfoDto.getMobiltelefonnummer()).isEqualTo("11111111");
    }

    @Test
    public void skal_mappe_feil_til_runtimeException() {
        String json = toJson(FEIL_JSON);
        Foedselsnummer foedselsnummer = Foedselsnummer.of("23067844539");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> KrrClient.parse(json, foedselsnummer));
        assertThat(runtimeException.getMessage())
                .isEqualTo("Henting av kontaktinfo fra KRR feilet: fant ikke person");
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
