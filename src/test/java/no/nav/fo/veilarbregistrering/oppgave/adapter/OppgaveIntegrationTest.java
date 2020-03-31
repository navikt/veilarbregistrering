package no.nav.fo.veilarbregistrering.oppgave.adapter;

import com.google.common.net.MediaType;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppgave.*;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
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

public class OppgaveIntegrationTest {

    private static final String MOCKSERVER_URL = "localhost";
    private static final int MOCKSERVER_PORT = 1081;

    private ClientAndServer mockServer;
    private OppgaveGateway oppgaveGateway;
    private OppgaveRepository oppgaveRepository;
    private UnleashService unleashService;

    private OppgaveService oppgaveService;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeEach
    public void setup() {
        oppgaveRepository = mock(OppgaveRepository.class);
        unleashService = mock(UnleashService.class);
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
        oppgaveGateway = new OppgaveGatewayImpl(buildClient());

        oppgaveService = new OppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                aktorId -> { },
                unleashService);
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
        String dagensdato = LocalDate.now().toString();
        String to_dager_senere = LocalDate.now().plusDays(2).toString();

        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/oppgaver")
                        .withBody("{" +
                                "\"aktoerId\":\"12e1e3\"," +
                                "\"beskrivelse\":\"" +
                                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                                "og har selv opprettet denne oppgaven. " +
                                "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.\"," +
                                "\"tema\":\"OPP\"," +
                                "\"oppgavetype\":\"KONT_BRUK\"," +
                                "\"fristFerdigstillelse\":\"" +
                                to_dager_senere +
                                "\"," +
                                "\"aktivDato\":\"" +
                                dagensdato +
                                "\"," +
                                "\"prioritet\":\"NORM\"" +
                                "}"))
                .respond(response()
                        .withStatusCode(201)
                        .withBody(okRegistreringBody(), MediaType.JSON_UTF_8));

        Oppgave oppgave = SubjectHandler.withSubject(
                new Subject("foo", IdentType.EksternBruker, SsoToken.oidcToken("bar", new HashMap<>())),
                () -> oppgaveService.opprettOppgave(
                        Bruker.of(Foedselsnummer.of("12345678911"), AktorId.valueOf("12e1e3")),
                        OppgaveType.OPPHOLDSTILLATELSE));

        assertThat(oppgave.getId()).isEqualTo(5436732);
        assertThat(oppgave.getTildeltEnhetsnr()).isEqualTo("3012");
    }

    @Test
    public void opprettelse_av_oppgave_skal_håndtere_null_for_oppgavetype() {
        String dagensdato = LocalDate.now().toString();
        String to_dager_senere = LocalDate.now().plusDays(2).toString();

        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/oppgaver")
                        .withBody("{" +
                                "\"aktoerId\":\"12e1e3\"," +
                                "\"beskrivelse\":\"" +
                                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                                "og har selv opprettet denne oppgaven. " +
                                "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.\"," +
                                "\"tema\":\"OPP\"," +
                                "\"oppgavetype\":\"KONT_BRUK\"," +
                                "\"fristFerdigstillelse\":\"" +
                                to_dager_senere +
                                "\"," +
                                "\"aktivDato\":\"" +
                                dagensdato +
                                "\"," +
                                "\"prioritet\":\"NORM\"" +
                                "}"))
                .respond(response()
                        .withStatusCode(201)
                        .withBody(okRegistreringBody(), MediaType.JSON_UTF_8));

        Oppgave oppgave = SubjectHandler.withSubject(
                new Subject("foo", IdentType.EksternBruker, SsoToken.oidcToken("bar", new HashMap<>())),
                () -> oppgaveService.opprettOppgave(
                        Bruker.of(Foedselsnummer.of("12345678911"), AktorId.valueOf("12e1e3")),
                        null));

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
