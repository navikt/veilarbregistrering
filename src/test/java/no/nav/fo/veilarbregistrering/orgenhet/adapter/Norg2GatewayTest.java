package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.nav.fo.veilarbregistrering.FileToJson;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static no.nav.fo.veilarbregistrering.arbeidssoker.adapter.baseclient.RestUtils.dummyResponseBuilder;
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
        assertThat(enhetsnr).hasValue(Enhetnr.Companion.of("0393"));
    }

    private static class Norg2StubClient extends Norg2RestClient {

        private final Gson gson = new GsonBuilder().create();

        private static final String OK_JSON = "/orgenhet/orgenhet.json";

        Norg2StubClient() {
            super(null);
        }

        @Override
        Response utfoerRequest(RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto) {
            String json = FileToJson.toJson(OK_JSON);
            RsNavKontorDto[] rsNavKontorDto = gson.fromJson(json, RsNavKontorDto[].class);

            final Response response =
                dummyResponseBuilder()
                        .code(200)
                        .body(ResponseBody.create(MediaType.parse("application/json"), gson.toJson(rsNavKontorDto)))
                        .build();

            return response;
        }
    }
}
