package no.nav.fo.veilarbregistrering.bruker.adapter;

import com.google.common.net.MediaType;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class PersonGatewayTest {

    private static final String MOCKSERVER_URL = "localhost";
    private static final int MOCKSERVER_PORT = 1080;

    private ClientAndServer mockServer;
    private VeilArbPersonClient veilArbPersonClient;
    private PersonGateway personGateway;

    @Before
    public void setup() {
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);

        veilArbPersonClient = buildClient();
        personGateway = new PersonGatewayImpl(veilArbPersonClient);
    }

    @After
    public void tearDown() {
        mockServer.stop();
    }

    private VeilArbPersonClient buildClient() {
        SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
        Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(any())).thenReturn("");
        when(systemUserTokenProvider.getSystemUserAccessToken()).thenReturn("testToken");
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT;
        VeilArbPersonClient veilArbPersonClient = this.veilArbPersonClient = new VeilArbPersonClient(baseUrl, httpServletRequestProvider, systemUserTokenProvider);
        return veilArbPersonClient;
    }

    @Test
    public void hentGeografiskTilknytning_skal_returnere_kontorid() {
        Foedselsnummer foedselsnummer = Foedselsnummer.of("12345678910");

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/person/geografisktilknytning")
                        .withQueryStringParameter("fnr", foedselsnummer.stringValue()))
                .respond(response()
                        .withBody("{\"geografiskTilknytning\": "+"1234"+"}", MediaType.JSON_UTF_8)
                        .withStatusCode(200));

        Optional<GeografiskTilknytning> geografiskTilknytning = personGateway.hentGeografiskTilknytning(foedselsnummer);

        assertThat(geografiskTilknytning).hasValue(GeografiskTilknytning.of("1234"));
    }

    @Test
    public void hentGeografiskTilknytning_skal_returnere_optional_hvis_404() {
        Foedselsnummer foedselsnummer = Foedselsnummer.of("12345678910");

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/person/geografisktilknytning")
                        .withQueryStringParameter("fnr", foedselsnummer.stringValue()))
                .respond(response()
                        .withStatusCode(404));

        Optional<GeografiskTilknytning> geografiskTilknytning = personGateway.hentGeografiskTilknytning(foedselsnummer);

        assertThat(geografiskTilknytning).isEmpty();
    }

    @Test
    public void hentGeografiskTilknytning_skal_returnere_optional_hvis_tom_tekst() {
        Foedselsnummer foedselsnummer = Foedselsnummer.of("12345678910");

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/person/geografisktilknytning")
                        .withQueryStringParameter("fnr", foedselsnummer.stringValue()))
                .respond(response()
                        .withBody("{\"geografiskTilknytning\": "+"null"+"}", MediaType.JSON_UTF_8)
                        .withStatusCode(200));

        Optional<GeografiskTilknytning> geografiskTilknytning = personGateway.hentGeografiskTilknytning(foedselsnummer);

        assertThat(geografiskTilknytning).isEmpty();
    }
}
