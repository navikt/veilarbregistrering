package no.nav.fo.veilarbregistrering.oppgave.adapter;

import com.google.common.net.MediaType;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;
import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class OppgaveGatewayTest {

    private static final String MOCKSERVER_URL = "localhost";
    private static final int MOCKSERVER_PORT = 1080;

    private ClientAndServer mockServer;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeEach
    public void setup() {
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
    }

    private OppgaveRestClient buildClient() {
        SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
        Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(any())).thenReturn("");
        when(systemUserTokenProvider.getToken()).thenReturn("testToken");
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT;
        OppgaveRestClient oppgaveRestClient = new OppgaveRestClient(baseUrl, httpServletRequestProvider);
        oppgaveRestClient.settSystemUserTokenProvider(systemUserTokenProvider);
        return oppgaveRestClient;
    }

    @Test
    public void vellykket_opprettelse_av_oppgave_skal_gi_201() {

        OppgaveGateway oppgaveGateway = new OppgaveGatewayImpl(buildClient());

        String dagensdato = LocalDate.now().toString();
        String to_dager_senere = LocalDate.now().plusDays(2).toString();

        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/oppgaver")
                        .withBody("{" +
                                "\"aktoerId\":\"12e1e3\"," +
                                "\"beskrivelse\":\"" +
                                "Denne oppgaven har bruker selv opprettet, og er en pilotering på NAV Grünerløkka." +
                                " Brukeren får ikke registrert seg som arbeidssøker." +
                                " Kontaktperson ved NAV Grünerløkka er Avdullah Demiri.\"," +
                                "\"tilordnetRessurs\":\"D113328\"," +
                                "\"tema\":\"OPP\"," +
                                "\"oppgavetype\":\"KONT_BRUK\"," +
                                "\"fristFerdigstillelse\":\"" +
                                to_dager_senere +
                                "\"," +
                                "\"aktivDato\":\"" +
                                dagensdato +
                                "\"," +
                                "\"prioritet\":\"LAV\"" +
                                "}"))
                .respond(response()
                        .withStatusCode(201)
                        .withBody(okRegistreringBody(), MediaType.JSON_UTF_8));

        Oppgave oppgave = SubjectHandler.withSubject(
                new Subject("foo", IdentType.EksternBruker, SsoToken.oidcToken("bar", new HashMap<>())),
                () -> oppgaveGateway.opprettOppgave("12e1e3")
        );

        assertThat(oppgave.getId()).isEqualTo(5436732);
        assertThat(oppgave.getTildeltEnhetsnr()).isEqualTo("3012");
    }

    private String okRegistreringBody() {
        return "{\n" +
                "\"id\": \"5436732\",\n" +
                "\"aktoerId\": \"12e1e3\",\n" +
                "\"tildeltEnhetsnr\": \"3012\"\n" +
                "}";
    }

}
