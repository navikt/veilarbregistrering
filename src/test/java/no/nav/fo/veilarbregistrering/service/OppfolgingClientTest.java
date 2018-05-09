package no.nav.fo.veilarbregistrering.service;

import com.google.common.net.MediaType;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.httpclient.SystemUserAuthorizationInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.service.StartRegistreringUtilsService.MAX_ALDER_AUTOMATISK_REGISTRERING;
import static no.nav.fo.veilarbregistrering.service.StartRegistreringUtilsService.MIN_ALDER_AUTOMATISK_REGISTRERING;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class OppfolgingClientTest {
    private static final String MOCKSERVER_URL = "localhost";
    private static final int MOCKSERVER_PORT = 1080;

    private ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private AktorService aktorService;
    private BrukerRegistreringService brukerRegistreringService;
    private OppfolgingClient oppfolgingClient;
    private ArbeidsforholdService arbeidsforholdService;
    private RemoteFeatureConfig.RegistreringFeature registreringFeature;
    private StartRegistreringUtilsService startRegistreringUtilsService;
    private ClientAndServer mockServer;
    private String ident;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeEach
    public void setup() {
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
        registreringFeature = mock(RemoteFeatureConfig.RegistreringFeature.class);
        aktorService = mock(AktorService.class);
        oppfolgingClient = buildClient();
        arbeidssokerregistreringRepository = mock(ArbeidssokerregistreringRepository.class);
        arbeidsforholdService = mock(ArbeidsforholdService.class);
        startRegistreringUtilsService = mock(StartRegistreringUtilsService.class);
        ident = "12345";

        brukerRegistreringService =
                new BrukerRegistreringService(
                        arbeidssokerregistreringRepository,
                        aktorService,
                        registreringFeature,
                        oppfolgingClient,
                        arbeidsforholdService,
                        startRegistreringUtilsService);


        System.setProperty(MIN_ALDER_AUTOMATISK_REGISTRERING, "30");
        System.setProperty(MAX_ALDER_AUTOMATISK_REGISTRERING, "59");

        when(startRegistreringUtilsService.oppfyllerKravOmAutomatiskRegistrering(any(), any(), any(), any())).thenReturn(true);
        when(aktorService.getAktorId(any())).thenReturn(Optional.of("AKTORID"));
        when(registreringFeature.erAktiv()).thenReturn(true);
    }

    private OppfolgingClient buildClient() {
        SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
        when(systemUserTokenProvider.getToken()).thenReturn("testToken");
        SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor = new SystemUserAuthorizationInterceptor(systemUserTokenProvider);
        return oppfolgingClient = new OppfolgingClient("http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT, systemUserAuthorizationInterceptor);
    }

    @Test
    public void testAtGirNotFoundExceptionDersomBrukerIkkeFinnesIOppfolging() {
        mockOppfolgingApiOK();
        mockServer.when(request().withMethod("POST").withPath("/api/aktiverbruker")).respond(response().withStatusCode(404));
        BrukerRegistrering brukerRegistrering = lagRegistreringGyldigBruker();
        assertThrows(NotFoundException.class, () -> brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }


    @Test
    public void testAtGirUnauthorizedExceptionDersomBrukerIkkkeHarTilgangTilOppfolging() {
        mockOppfolgingApiOK();
        mockServer.when(request().withMethod("POST").withPath("/api/aktiverbruker")).respond(response().withStatusCode(401));
        BrukerRegistrering brukerRegistrering = lagRegistreringGyldigBruker();
        assertThrows(NotAuthorizedException.class, () -> brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }

    @Test
    public void testAtGirServerErrorExceptionDersomAktiverBrukerFeiler() {
        mockOppfolgingApiOK();
        mockServer.when(request().withMethod("POST").withPath("/api/aktiverbruker")).respond(response().withStatusCode(502));
        BrukerRegistrering brukerRegistrering = lagRegistreringGyldigBruker();
        assertThrows(ServerErrorException.class, () -> brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }

    @Test
    public void testAtGirUnauthorizedExceptionDersomBrukerIkkkeHarTilgangTilOppfolgingStatus() {
        mockServer.when(request().withMethod("GET").withPath("/api/oppfolging")).respond(response().withStatusCode(401));
        mockServer.when(request().withMethod("GET").withPath("/api/person/" + ident + "oppfoelgingsstatus")).respond(response().withStatusCode(401));
        assertThrows(NotAuthorizedException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirServerErrorExceptionDersomKunOppfolgingFeiler() {
        mockServer.when(request().withMethod("GET").withPath("/api/oppfolging")).respond(response().withStatusCode(500));
        mockServer.when(request().withMethod("GET").withPath("/api/person/" + ident + "oppfoelgingsstatus")).respond(response().withStatusCode(200));
        assertThrows(ServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirServerErrorExceptionDersomKunOppfolgingStatusFeiler() {
        mockServer.when(request().withMethod("GET").withPath("/api/oppfolging"))
                .respond(response().withBody(oppfolgingBody(), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("GET").withPath("/api/person/" + ident + "oppfoelgingsstatus"))
                .respond(response().withBody(oppfolgingStatusBody(), MediaType.JSON_UTF_8).withStatusCode(500));

        assertThrows(ServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirIngenExceptionsDersomKun200OK() {
        mockServer.when(request().withMethod("GET").withPath("/api/oppfolging"))
                .respond(response().withBody(oppfolgingBody(), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("GET").withPath("/api/person/" + ident + "oppfoelgingsstatus"))
                .respond(response().withBody(oppfolgingStatusBody(), MediaType.JSON_UTF_8).withStatusCode(200));

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    private void mockOppfolgingApiOK() {
        mockServer.when(request().withMethod("GET").withPath("/api/oppfolging")).respond(response().withStatusCode(200));
        mockServer.when(request().withMethod("GET").withPath("/api/person/" + ident + "oppfoelgingsstatus")).respond(response().withStatusCode(200));
    }

    private String oppfolgingBody() {
        return "{\n" +
                "\"fnr\": 12345678910,\n" +
                "\"veilederId\": null,\n" +
                "\"reservasjonKRR\": false,\n" +
                "\"manuell\": false,\n" +
                "\"underOppfolging\": true,\n" +
                "\"underKvp\": false,\n" +
                "\"vilkarMaBesvares\": true,\n" +
                "\"oppfolgingUtgang\": null,\n" +
                "\"gjeldendeEskaleringsvarsel\": null,\n" +
                "\"kanStarteOppfolging\": false,\n" +
                "\"avslutningStatus\": null,\n" +
                "\"oppfolgingsPerioder\": [],\n" +
                "\"harSkriveTilgang\": true\n" +
                "}";
    }

    private String oppfolgingStatusBody() {
        return "{\n" +
                "\"rettighetsgruppe\": \"IYT\",\n" +
                "\"formidlingsgruppe\": \"ARBS\",\n" +
                "\"servicegruppe\": \"IKVAL\",\n" +
                "\"oppfolgingsenhet\": {\n" +
                "\"navn\": \"NAV Aremark\",\n" +
                "\"enhetId\": \"0118\"\n" +
                "},\n" +
                "\"inaktiveringsdato\": \"2018-03-08T12:00:00+01:00\"" +
                "}";
    }

    private BrukerRegistrering lagRegistreringGyldigBruker() {
        return BrukerRegistrering.builder()
                .enigIOppsummering(true)
                .harHelseutfordringer(false)
                .nusKode("12312")
                .yrkesPraksis("1234")
                .build();
    }
}