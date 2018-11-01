package no.nav.fo.veilarbregistrering.service;

import com.google.common.net.MediaType;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.httpclient.DigisyfoClient;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.veilarbregistrering.TestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.System.setProperty;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.gyldigBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.lagProfilering;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class OppfolgingClientTest {
    private static final String MOCKSERVER_URL = "localhost";
    private static final int MOCKSERVER_PORT = 1080;

    private RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature;
    private ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private AktorService aktorService;
    private BrukerRegistreringService brukerRegistreringService;
    private OppfolgingClient oppfolgingClient;
    private DigisyfoClient sykeforloepMetadataClient;
    private ArbeidsforholdService arbeidsforholdService;
    private StartRegistreringUtils startRegistreringUtils;
    private ClientAndServer mockServer;
    private String ident;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeAll
    public static void before() {
        TestContext.setup();
    }

    @BeforeEach
    public void setup() {

        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
        sykemeldtRegistreringFeature = mock(RemoteFeatureConfig.SykemeldtRegistreringFeature.class);
        aktorService = mock(AktorService.class);
        oppfolgingClient = buildClient();
        arbeidssokerregistreringRepository = mock(ArbeidssokerregistreringRepository.class);
        arbeidsforholdService = mock(ArbeidsforholdService.class);
        sykeforloepMetadataClient = mock(DigisyfoClient.class);
        startRegistreringUtils = mock(StartRegistreringUtils.class);
        ident = "10108000398"; //Aremark fiktivt fnr.";

        brukerRegistreringService =
                new BrukerRegistreringService(
                        arbeidssokerregistreringRepository,
                        aktorService,
                        oppfolgingClient,
                        sykeforloepMetadataClient,
                        arbeidsforholdService,
                        startRegistreringUtils,
                        sykemeldtRegistreringFeature);


        when(startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(any(), any())).thenReturn(true);
        when(aktorService.getAktorId(any())).thenReturn(Optional.of("AKTORID"));
        when(sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()).thenReturn(true);
    }

    private OppfolgingClient buildClient() {
        SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
        Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(any())).thenReturn("");
        when(systemUserTokenProvider.getToken()).thenReturn("testToken");
        setProperty("VEILARBOPPFOLGINGAPI_URL", "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT);
        return oppfolgingClient = new OppfolgingClient(httpServletRequestProvider);
    }

    @Test
    public void testAtGirRuntimeExceptionDersomOppfolgingIkkeSvarer() {
        mockIkkeUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(404));
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering();
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }


    @Test
    public void testAtGirInternalErrorExceptionDersomBrukerIkkkeHarTilgangTilOppfolging() {
        mockUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(401));
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering();
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }


    @Test
    public void testAtRegistreringGirOKDersomBrukerIkkeHarOppfolgingsflaggOgIkkeErAktivIArena() {
        when(arbeidssokerregistreringRepository.lagreBruker(any(), any())).thenReturn(new BrukerRegistrering());
        when(startRegistreringUtils.profilerBruker(any(), anyInt(), any(), any())).thenReturn(lagProfilering());
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(harIkkeOppfolgingsflaggOgErInaktivIArenaBody(), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(204).withBody(okRegistreringBody(), MediaType.JSON_UTF_8));

        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering();
        assertNotNull(brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }

    @Test
    public void testAtReaktiveringFeilerDersomArenaSierAtBrukerHarAktivStatus() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(harOppfolgingsflaggOgErAktivIArenaBody(), MediaType.JSON_UTF_8).withStatusCode(200));

        assertThrows(RuntimeException.class, () -> brukerRegistreringService.reaktiverBruker(ident));
    }

    @Test
    public void testAtReaktiveringGirOKDersomArenaSierAtBrukerHarInaktivStatus() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(harIkkeOppfolgingsflaggOgKanReaktiveres(LocalDateTime.now().minusDays(20)), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/reaktiverbruker")).respond(response().withStatusCode(204));

        brukerRegistreringService.reaktiverBruker(ident);
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomAktiverBrukerFeiler() {
        mockUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(502));
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering();
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomBrukerIkkkeHarTilgangTilOppfolgingStatus() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(401));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomOppfolgingFeiler() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(500));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirIngenExceptionsDersomKun200OK() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(harOppfolgingsflaggOgErAktivIArenaBody(), MediaType.JSON_UTF_8).withStatusCode(200));

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirIngenExceptionsDersomKun200MedKanReaktiveresNull() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(harOppfolgingsflaggOgErAktivIArenaKanReaktiveresNullBody(), MediaType.JSON_UTF_8).withStatusCode(200));

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    private void mockUnderOppfolgingApi() {
        mockServer.when(request().withMethod("GET").withPath("/person/" + ident + "/aktivstatus"))
                .respond(response().withBody(harOppfolgingsflaggOgErAktivIArenaBody()).withStatusCode(200));
    }

    private void mockIkkeUnderOppfolgingApi() {
        mockServer.when(request().withMethod("GET").withPath("/person/" + ident + "/aktivstatus"))
                .respond(response().withBody(harIkkeOppfolgingsflaggOgErInaktivIArenaBody(), MediaType.JSON_UTF_8).withStatusCode(200));
    }

    private String harIkkeOppfolgingsflaggOgKanReaktiveres(LocalDateTime inaktiveringsdato) {
        return "{\n" +
                "\"kanReaktiveres\": true,\n" +
                "\"underOppfolging\": false\n" +
                "}";
    }

    private String harOppfolgingsflaggOgErAktivIArenaBody() {
        return "{\n" +
                "\"kanReaktiveres\": false,\n" +
                "\"underOppfolging\": true\n" +
                "}";
    }

    private String harOppfolgingsflaggOgErAktivIArenaKanReaktiveresNullBody() {
        return "{\n" +
                "\"kanReaktiveres\": null,\n" +
                "\"underOppfolging\": true\n" +
                "}";
    }

    private String harIkkeOppfolgingsflaggOgErInaktivIArenaBody() {
        return "{\n" +
                "\"kanReaktiveres\": false,\n" +
                "\"underOppfolging\": false\n" +
                "}";
    }


    private String okRegistreringBody() {
        return "{\n" +
                "\"Status\": \"STATUS_SUKSESS\"\n" +
                "}";
    }
}