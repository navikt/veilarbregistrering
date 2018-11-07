package no.nav.fo.veilarbregistrering.service;

import com.google.common.net.MediaType;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.fo.veilarbregistrering.domain.SykmeldtRegistrering;
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
import java.util.Optional;

import static java.lang.System.setProperty;
import static no.nav.fo.veilarbregistrering.domain.RegistreringType.SYKMELDT_REGISTRERING;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.gyldigSykmeldtRegistrering;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class DigisyfoClientTest {
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
        oppfolgingClient = buildOppfolgingClient();
        arbeidssokerregistreringRepository = mock(ArbeidssokerregistreringRepository.class);
        arbeidsforholdService = mock(ArbeidsforholdService.class);
        sykeforloepMetadataClient = buildSykeForloepClient();
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
        when(sykemeldtRegistreringFeature.skalKalleDigisyfoTjeneste()).thenReturn(true);
        when(sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()).thenReturn(true);
    }

    private DigisyfoClient buildSykeForloepClient() {
        Provider<HttpServletRequest> httpServletRequestProvider = new ConfigBuildClient().invoke();
        setProperty("SYKEFRAVAERAPI_URL", "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT + "/");
        return sykeforloepMetadataClient = new DigisyfoClient(httpServletRequestProvider);
    }

    private OppfolgingClient buildOppfolgingClient() {
        Provider<HttpServletRequest> httpServletRequestProvider = new ConfigBuildClient().invoke();
        setProperty("VEILARBOPPFOLGINGAPI_URL", "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT);
        return oppfolgingClient = new OppfolgingClient(httpServletRequestProvider);
    }

    @Test
    public void testAtRegistreringAvSykmeldtGirOk() {
        mockSykmeldtIArena();
        mockSykmeldtOver39u();
        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverSykmeldt")).respond(response().withStatusCode(204));
        brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, ident);
    }

    @Test
    public void testAtHentingAvSykeforloepMetadataGirOk() {
        mockSykmeldtIArena();
        mockSykmeldtOver39u();
        StartRegistreringStatus startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(ident);
        assertTrue(startRegistreringStatus.getRegistreringType() == SYKMELDT_REGISTRERING);
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomRegistreringAvSykmeldtFeiler() {
        mockSykmeldtIArena();
        mockSykmeldtOver39u();
        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverSykmeldt")).respond(response().withStatusCode(502));
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, ident));
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomHentingAvSykeforloepMetadataFeiler() {
        mockSykmeldtIArena();
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    private void mockSykmeldtOver39u() {
        mockServer.when(request().withMethod("GET").withPath("/sykeforloep/metadata"))
                .respond(response().withBody(sykmeldtOver39u(), MediaType.JSON_UTF_8).withStatusCode(200));
    }

    private void mockSykmeldtIArena() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(harIkkeOppfolgingsflaggOgErInaktivIArenaBody(), MediaType.JSON_UTF_8).withStatusCode(200));
    }

    private String sykmeldtOver39u() {
        return "{\n" +
                "\"erArbeidsrettetOppfolgingSykmeldtInngangAktiv\": true\n" +
                "}";
    }

    private String harIkkeOppfolgingsflaggOgErInaktivIArenaBody() {
        return "{\n" +
                "\"erSykmeldtMedArbeidsgiver\": true\n" +
                "}";
    }

    private class ConfigBuildClient {
        public Provider<HttpServletRequest> invoke() {
            SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
            Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
            HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
            when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader(any())).thenReturn("");
            when(systemUserTokenProvider.getToken()).thenReturn("testToken");
            return httpServletRequestProvider;
        }
    }
}