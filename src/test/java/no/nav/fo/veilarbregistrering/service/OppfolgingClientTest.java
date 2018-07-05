package no.nav.fo.veilarbregistrering.service;

import com.google.common.net.MediaType;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.HelseHinderSvar;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.httpclient.SystemUserAuthorizationInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.service.StartRegistreringUtilsService.MAX_ALDER_AUTOMATISK_REGISTRERING;
import static no.nav.fo.veilarbregistrering.service.StartRegistreringUtilsService.MIN_ALDER_AUTOMATISK_REGISTRERING;
import static org.assertj.core.api.Assertions.assertThat;
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
        ident = "10108000398"; //Aremark fiktivt fnr.";

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

        when(startRegistreringUtilsService.harJobbetSammenhengendeSeksAvTolvSisteManeder(any(), any())).thenReturn(true);
        when(startRegistreringUtilsService.oppfyllerBetingelseOmInaktivitet(any(), any())).thenReturn(true);
        when(aktorService.getAktorId(any())).thenReturn(Optional.of("AKTORID"));
        when(registreringFeature.erAktiv()).thenReturn(true);
    }

    private OppfolgingClient buildClient() {
        SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
        Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(any())).thenReturn("");
        when(systemUserTokenProvider.getToken()).thenReturn("testToken");
        SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor = new SystemUserAuthorizationInterceptor(systemUserTokenProvider);
        return oppfolgingClient = new OppfolgingClient("http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT, systemUserAuthorizationInterceptor, httpServletRequestProvider);
    }

    @Test
    public void testAtGirRuntimeExceptionDersomOppfolgingIkkeSvarer() {
        mockIkkeUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(404));
        BrukerRegistrering brukerRegistrering = lagRegistreringGyldigBruker();
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }


    @Test
    public void testAtGirInternalErrorExceptionDersomBrukerIkkkeHarTilgangTilOppfolging() {
        mockUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(401));
        BrukerRegistrering brukerRegistrering = lagRegistreringGyldigBruker();
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }


    @Test
    public void testAtRegistreringGirOKDeromBrukerIkkeHarOppfolgingsflaggOgIkkeErAktivIArena() {
        when(arbeidssokerregistreringRepository.lagreBruker(any(),any())).thenReturn(new BrukerRegistrering());
        mockServer.when(request().withMethod("GET").withPath("/person/" + ident + "/aktivstatus"))
                .respond(response().withBody(harIkkeOppfolgingsflaggOgErInaktivIArenaBody(), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(204).withBody(okRegistreringBody(), MediaType.JSON_UTF_8));

        BrukerRegistrering brukerRegistrering = lagRegistreringGyldigBruker();
        assertNotNull(brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }

    @Test
    public void testAtRegistreringIkkeGjoresDeromBrukerHarOppfolgingsflaggMenIkkeErAktivIArena() {
        when(arbeidssokerregistreringRepository.lagreBruker(any(),any())).thenReturn(new BrukerRegistrering());
        mockServer.when(request().withMethod("GET").withPath("/person/" + ident + "/aktivstatus"))
                .respond(response().withBody(harOppfolgingsflaggOgErInaktivIArenaBody(), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(204));

        StartRegistreringStatus startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(ident);
        assertThat(startRegistreringStatus.isUnderOppfolging()).isFalse();
    }


    @Test
    public void testAtGirInternalServerErrorExceptionDersomAktiverBrukerFeiler() {
        mockUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(502));
        BrukerRegistrering brukerRegistrering = lagRegistreringGyldigBruker();
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.registrerBruker(brukerRegistrering, ident));
    }

    @Test
    public void testAtGirUnauthorizedExceptionDersomBrukerIkkkeHarTilgangTilOppfolgingStatus() {
        mockServer.when(request().withMethod("GET").withPath("/person/" + ident + "/aktivstatus")).respond(response().withStatusCode(401));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomOppfolgingFeiler() {
        mockServer.when(request().withMethod("GET").withPath("/person/" + ident + "/aktivstatus")).respond(response().withStatusCode(500));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirIngenExceptionsDersomKun200OK() {
        mockServer.when(request().withMethod("GET").withPath("/person/" + ident + "/aktivstatus"))
                .respond(response().withBody(harOppfolgingsflaggOgErAktivIArenaBody(), MediaType.JSON_UTF_8).withStatusCode(200));

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

    private String harOppfolgingsflaggOgErAktivIArenaBody() {
        return "{\n" +
                "\"aktiv\": true,\n" +
                "\"underOppfolging\": true\n" +
                "}";
    }

    private String harIkkeOppfolgingsflaggOgErInaktivIArenaBody() {
        return "{\n" +
                "\"aktiv\": false,\n" +
                "\"inaktiveringsdato\": \"2018-03-08T12:00:00+01:00\",\n" +
                "\"underOppfolging\": false\n" +
                "}";
    }

    private String harOppfolgingsflaggOgErInaktivIArenaBody() {
        return "{\n" +
                "\"aktiv\": false,\n" +
                "\"inaktiveringsdato\": \"2018-03-08T12:00:00+01:00\",\n" +
                "\"underOppfolging\": true\n" +
                "}";
    }

    private String okRegistreringBody() {
        return "{\n" +
                "\"Status\": \"STATUS_SUKSESS\"\n" +
                "}";
    }

    private BrukerRegistrering lagRegistreringGyldigBruker() {
        return new BrukerRegistrering()
                .setEnigIOppsummering(true)
                .setBesvarelse(new Besvarelse().setHelseHinder(HelseHinderSvar.NEI))
                .setNusKode("12312")
                .setSisteStilling(new Stilling().setStyrk08("1234"));
    }
}