package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import com.google.common.net.MediaType;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringStatus;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.veilarbregistrering.TestContext;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.SYKMELDT_REGISTRERING;
import static no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class SykmeldtInfoClientTest {
    private static final String MOCKSERVER_URL = "localhost";
    private static final int MOCKSERVER_PORT = 1080;

    private RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature;
    private BrukerRegistreringRepository brukerRegistreringRepository;
    private ProfileringRepository profileringRepository;
    private BrukerRegistreringService brukerRegistreringService;
    private OppfolgingClient oppfolgingClient;
    private SykmeldtInfoClient sykeforloepMetadataClient;
    private ArbeidsforholdGateway arbeidsforholdGateway;
    private ManuellRegistreringService manuellRegistreringService;
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
        oppfolgingClient = buildOppfolgingClient();
        brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);
        profileringRepository = mock(ProfileringRepository.class);
        arbeidsforholdGateway = mock(ArbeidsforholdGateway.class);
        sykeforloepMetadataClient = buildSykeForloepClient();
        startRegistreringUtils = mock(StartRegistreringUtils.class);
        manuellRegistreringService = mock(ManuellRegistreringService.class);
        ident = "10108000398"; //Aremark fiktivt fnr.";

        brukerRegistreringService =
                new BrukerRegistreringService(
                        brukerRegistreringRepository,
                        profileringRepository,
                        new OppfolgingGatewayImpl(oppfolgingClient),
                        new SykemeldingService(new SykemeldingGatewayImpl(sykeforloepMetadataClient)),
                        arbeidsforholdGateway,
                        manuellRegistreringService,
                        startRegistreringUtils,
                        sykemeldtRegistreringFeature);


        when(startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(any(), any())).thenReturn(true);
        when(sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()).thenReturn(true);
    }

    private SykmeldtInfoClient buildSykeForloepClient() {
        Provider<HttpServletRequest> httpServletRequestProvider = new ConfigBuildClient().invoke();
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT + "/";
        return sykeforloepMetadataClient = new SykmeldtInfoClient(baseUrl, httpServletRequestProvider);
    }

    private OppfolgingClient buildOppfolgingClient() {
        Provider<HttpServletRequest> httpServletRequestProvider = new ConfigBuildClient().invoke();
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT;
        return oppfolgingClient = new OppfolgingClient(baseUrl, httpServletRequestProvider);
    }

    @Test
    @Disabled
    public void testAtRegistreringAvSykmeldtGirOk() {
        mockSykmeldtIArena();
        mockSykmeldtOver39u();
        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverSykmeldt")).respond(response().withStatusCode(204));
        brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, Bruker.fraFnr(ident).medAktoerId("AKTØRID"));
    }

    @Test
    @Disabled
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
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, Bruker.fraFnr(ident).medAktoerId("AKTØRID")));
    }

    // TODO: FIKS når infotrygd api er klar
//    @Test
//    public void testAtGirInternalServerErrorExceptionDersomHentingAvSykeforloepMetadataFeiler() {
//        mockSykmeldtIArena();
//        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
//    }

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
