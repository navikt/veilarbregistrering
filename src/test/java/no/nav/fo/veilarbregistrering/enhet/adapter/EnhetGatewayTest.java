package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.NotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EnhetGatewayTest {

    private EnhetGatewayImpl enhetGateway;

    @BeforeEach
    public void setUp() {
        EnhetRestClient enhetStubClient = new EnhetStubClient();
        enhetGateway = new EnhetGatewayImpl(enhetStubClient);
    }

    @Test
    public void hentOrganisasjonsdetaljer_skal_kunne_hente_ut_kommunenummer_fra_enhetsregisteret() {
        Optional<Organisasjonsdetaljer> organisasjonsdetaljer
                = enhetGateway.hentOrganisasjonsdetaljer(Organisasjonsnummer.of("995298775"));

        assertThat(organisasjonsdetaljer).isNotEmpty();
        assertThat(organisasjonsdetaljer.get().kommunenummer()).hasValue(Kommunenummer.of("0301"));
    }

    @Test
    public void hentOrganisasjonsdetaljer_skal_gi_empty_result_ved_ukjent_org_nr() {
        Optional<Organisasjonsdetaljer> organisasjonsdetaljer
                = enhetGateway.hentOrganisasjonsdetaljer(Organisasjonsnummer.of("123456789"));

        assertThat(organisasjonsdetaljer).isEmpty();
    }

    @Test
    public void hentOrganisasjonsdetaljer_skal_kaste_runtimeException_ved_feil() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> enhetGateway.hentOrganisasjonsdetaljer(null));

        assertThat(runtimeException.getMessage()).isEqualTo("Hent organisasjon feilet");
    }

    private class EnhetStubClient extends EnhetRestClient {

        private static final String OK_JSON = "/enhet/enhet.json";

        private Map<Organisasjonsnummer, String> jsonResponse = new HashMap<>();

        EnhetStubClient() {
            super(null);
            jsonResponse.put(Organisasjonsnummer.of("995298775"), toJson(OK_JSON));
        }

        @Override
        String utfoerRequest(Organisasjonsnummer organisasjonsnummer) {

            if (organisasjonsnummer == null) {
                throw new RuntimeException("Stub: Hent organisasjon feilet");
            }

            String jsonResponse = this.jsonResponse.get(organisasjonsnummer);
            if (jsonResponse == null) {
                throw new NotFoundException("Stub: Fant ikke organisasjonsnummer");
            }
            return jsonResponse;
        }
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
