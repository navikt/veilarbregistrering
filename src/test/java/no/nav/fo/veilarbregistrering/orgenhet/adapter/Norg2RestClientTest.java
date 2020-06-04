package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static no.nav.fo.veilarbregistrering.FileToJson.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class Norg2RestClientTest {

    private static final String OK_JSON = "/orgenhet/orgenhet.json";

    private static final String MOCKSERVER_URL = "localhost";
    private static final int MOCKSERVER_PORT = 1080;

    private ClientAndServer mockServer;

    private Norg2RestClient norg2RestClient;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeEach
    public void setup() {
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
        norg2RestClient = buildClient();
    }

    @Disabled
    @Test
    public void skal_hente_nav_enhet_for_kommunenummer() {
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v1/arbeidsfordeling/enheter/bestmatch"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(toJson(OK_JSON)));

        List<RsNavKontorDto> rsNavKontorDtos = norg2RestClient.hentEnhetFor(Kommunenummer.of("0309"));
        assertThat(rsNavKontorDtos).isNotEmpty();
    }

    private Norg2RestClient buildClient() {
        Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(any())).thenReturn("");
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT;
        Norg2RestClient norg2RestClient = this.norg2RestClient = new Norg2RestClient(baseUrl);
        return norg2RestClient;
    }
}
