package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.nav.fo.veilarbregistrering.FileToJson;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Norg2GatewayTest {

    private Norg2Gateway norg2Gateway;

    @BeforeEach
    public void setUp() {
        Norg2RestClient norg2RestClient = new Norg2StubClient();
        norg2Gateway = new Norg2GatewayImpl(norg2RestClient);
    }

    @Test
    public void skal_hente_enhetsnr_fra_norg2_for_kommunenummer() {
        Optional<Enhetnr> enhetsnr = norg2Gateway.hentEnhetFor(Kommunenummer.of("0302"));
        assertThat(enhetsnr).isNotEmpty();
        assertThat(enhetsnr).hasValue(Enhetnr.of("0393"));
    }

    private class Norg2StubClient extends Norg2RestClient {

        private final Gson gson = new GsonBuilder().create();

        private static final String OK_JSON = "/orgenhet/orgenhet.json";

        Norg2StubClient() {
            super(null);
        }

        @Override
        Response utfoerRequest(RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto) {
            String json = FileToJson.toJson(OK_JSON);
            RsNavKontorDto[] rsNavKontorDto = gson.fromJson(json, RsNavKontorDto[].class);

            final Response response = Mockito.mock(Response.class);
            Mockito.when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
            Mockito.when(response.readEntity(Mockito.any(Class.class))).thenReturn(Arrays.asList(rsNavKontorDto));
            Mockito.when(response.readEntity(Mockito.any(GenericType.class))).thenReturn(Arrays.asList(rsNavKontorDto));

            return response;
        }
    }
}
