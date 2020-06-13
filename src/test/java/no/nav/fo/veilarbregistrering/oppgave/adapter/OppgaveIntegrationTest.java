package no.nav.fo.veilarbregistrering.oppgave.adapter;

import com.google.common.net.MediaType;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppgave.*;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class OppgaveIntegrationTest {

    private static final String MOCKSERVER_URL = "localhost";
    private static final int MOCKSERVER_PORT = 1081;
    public static final Bruker BRUKER = Bruker.of(Foedselsnummer.of("12345678911"), AktorId.of("12e1e3"));

    private ClientAndServer mockServer;

    private OppgaveService oppgaveService;
    private OppgaveRouter oppgaveRouter;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeEach
    public void setup() {
        OppgaveRepository oppgaveRepository = mock(OppgaveRepository.class);
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
        OppgaveGateway oppgaveGateway = new OppgaveGatewayImpl(buildClient());
        oppgaveRouter = mock(OppgaveRouter.class);

        oppgaveService = new CustomOppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                oppgaveRouter,
                (aktorId, oppgaveType) -> { }
        );
    }

    private OppgaveRestClient buildClient() {
        SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
        Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(any())).thenReturn("");
        when(systemUserTokenProvider.getSystemUserAccessToken()).thenReturn("testToken");
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT;
        OppgaveRestClient oppgaveRestClient = new OppgaveRestClient(baseUrl, systemUserTokenProvider);
        return oppgaveRestClient;
    }

    @Test
    public void vellykket_opprettelse_av_oppgave_skal_gi_201() {
        String dagensdato = LocalDate.of(2020, 5, 27).toString();
        String toArbeidsdagerSenere = LocalDate.of(2020, 5, 29).toString();

        when(oppgaveRouter.hentEnhetsnummerFor(BRUKER, OppgaveType.UTVANDRET)).thenReturn(Optional.of(Enhetsnr.of("0301")));

        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/oppgaver")
                        .withBody("{" +
                                "\"aktoerId\":\"12e1e3\"," +
                                "\"beskrivelse\":\"" +
                                "Brukeren får ikke registrert seg som arbeidssøker fordi bruker står som utvandret i TPS og ikke er befolket i Arena, " +
                                "og har selv opprettet denne oppgaven. " +
                                "Ring bruker og følg vanlig rutine for slike tilfeller.\"," +
                                "\"tema\":\"OPP\"," +
                                "\"oppgavetype\":\"KONT_BRUK\"," +
                                "\"fristFerdigstillelse\":\"" +
                                toArbeidsdagerSenere +
                                "\"," +
                                "\"aktivDato\":\"" +
                                dagensdato +
                                "\"," +
                                "\"prioritet\":\"NORM\"," +
                                "\"tildeltEnhetsnr\":\"0301\"" +
                                "}"))
                .respond(response()
                        .withStatusCode(201)
                        .withBody(okRegistreringBody(), MediaType.JSON_UTF_8));

        OppgaveResponse oppgaveResponse = SubjectHandler.withSubject(
                new Subject("foo", IdentType.EksternBruker, SsoToken.oidcToken("bar", new HashMap<>())),
                () -> oppgaveService.opprettOppgave(
                        BRUKER,
                        OppgaveType.UTVANDRET));

        assertThat(oppgaveResponse.getId()).isEqualTo(5436732);
        assertThat(oppgaveResponse.getTildeltEnhetsnr()).isEqualTo("3012");
    }

    private String okRegistreringBody() {
        return "{\n" +
                "\"id\": \"5436732\",\n" +
                "\"aktoerId\": \"12e1e3\",\n" +
                "\"tildeltEnhetsnr\": \"3012\"\n" +
                "}";
    }

    private static class CustomOppgaveService extends OppgaveService {

        public CustomOppgaveService(
                OppgaveGateway oppgaveGateway,
                OppgaveRepository oppgaveRepository,
                OppgaveRouter oppgaveRouter,
                KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer) {
            super(oppgaveGateway, oppgaveRepository, oppgaveRouter, kontaktBrukerHenvendelseProducer);
        }

        @Override
        protected LocalDate idag() {
            return LocalDate.of(2020, 5, 27);
        }
    }
}
