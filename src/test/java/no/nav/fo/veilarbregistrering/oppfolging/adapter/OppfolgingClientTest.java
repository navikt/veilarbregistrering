package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import com.google.common.net.MediaType;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.registrering.kafka.MeldingsSender;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;

import static no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
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
    private BrukerRegistreringRepository brukerRegistreringRepository;
    private ProfileringRepository profileringRepository;
    private BrukerRegistreringService brukerRegistreringService;
    private ManuellRegistreringService manuellRegistreringService;
    private OppfolgingClient oppfolgingClient;
    private PersonGateway personGateway;
    private SykmeldtInfoClient sykeforloepMetadataClient;
    private ArbeidsforholdGateway arbeidsforholdGateway;
    private StartRegistreringUtils startRegistreringUtils;
    private MeldingsSender meldingsSender;
    private ClientAndServer mockServer;
    private String ident;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeEach
    public void setup() {

        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
        sykemeldtRegistreringFeature = mock(RemoteFeatureConfig.SykemeldtRegistreringFeature.class);
        oppfolgingClient = buildClient();
        personGateway = mock(PersonGateway.class);

        brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);
        profileringRepository = mock(ProfileringRepository.class);
        arbeidsforholdGateway = mock(ArbeidsforholdGateway.class);
        sykeforloepMetadataClient = mock(SykmeldtInfoClient.class);
        startRegistreringUtils = mock(StartRegistreringUtils.class);
        manuellRegistreringService = mock(ManuellRegistreringService.class);
        meldingsSender = (aktorId) -> {};
        ident = "10108000398"; //Aremark fiktivt fnr.";

        brukerRegistreringService =
                new BrukerRegistreringService(
                        brukerRegistreringRepository,
                        profileringRepository,
                        new OppfolgingGatewayImpl(oppfolgingClient),
                        personGateway,
                        new SykemeldingService(new SykemeldingGatewayImpl(sykeforloepMetadataClient)),
                        arbeidsforholdGateway,
                        manuellRegistreringService,
                        startRegistreringUtils,
                        sykemeldtRegistreringFeature,
                        meldingsSender);


        when(startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(any(), any())).thenReturn(true);
        when(startRegistreringUtils.profilerBruker(anyInt(), any(), any(), any()))
                .thenReturn(new Profilering()
                        .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                        .setAlder(50)
                        .setJobbetSammenhengendeSeksAvTolvSisteManeder(true));
        when(sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()).thenReturn(true);
    }

    private OppfolgingClient buildClient() {
        SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
        Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(any())).thenReturn("");
        when(systemUserTokenProvider.getToken()).thenReturn("testToken");
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT;
        OppfolgingClient oppfolgingClient = this.oppfolgingClient = new OppfolgingClient(baseUrl, httpServletRequestProvider);
        oppfolgingClient.settSystemUserTokenProvider(systemUserTokenProvider);
        return oppfolgingClient;
    }

    @Test
    public void testAtGirRuntimeExceptionDersomOppfolgingIkkeSvarer() {
        mockIkkeUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(404));
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        when (brukerRegistreringRepository.lagreOrdinaerBruker(any(OrdinaerBrukerRegistrering.class), any(AktorId.class))).thenReturn(new OrdinaerBrukerRegistrering());
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, Bruker.fraFnr(ident).medAktoerId("AKTØRID")));
    }


    @Test
    public void testAtGirInternalErrorExceptionDersomBrukerIkkkeHarTilgangTilOppfolging() {
        mockUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(401));
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, Bruker.fraFnr(ident).medAktoerId("AKTØRID")));
    }


    @Test
    public void testAtRegistreringGirOKDersomBrukerIkkeHarOppfolgingsflaggOgIkkeSkalReaktiveres() {
        when(brukerRegistreringRepository.lagreOrdinaerBruker(any(), any())).thenReturn(new OrdinaerBrukerRegistrering());
        when(startRegistreringUtils.profilerBruker(anyInt(), any(), any(), any())).thenReturn(lagProfilering());
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(false, false), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(204).withBody(okRegistreringBody(), MediaType.JSON_UTF_8));

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        assertNotNull(brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, Bruker.fraFnr(ident).medAktoerId("AKTØRID")));
    }

    @Test
    public void testAtReaktiveringFeilerDersomArenaSierAtBrukerErUnderOppfolging() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(true, false), MediaType.JSON_UTF_8).withStatusCode(200));

        assertThrows(RuntimeException.class, () -> brukerRegistreringService.reaktiverBruker(Bruker.fraFnr(ident).medAktoerId("AKTØRID")));
    }

    @Test
    public void testAtReaktiveringGirOKDersomArenaSierAtBrukerKanReaktiveres() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(false, true), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/reaktiverbruker")).respond(response().withStatusCode(204));

        brukerRegistreringService.reaktiverBruker(Bruker.fraFnr(ident).medAktoerId("AKTØRID"));
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomAktiverBrukerFeiler() {
        mockUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(502));
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, Bruker.fraFnr(ident).medAktoerId("AKTØRID")));
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
    public void testAtGirWebApplicationExceptionExceptionDersomIngenTilgang() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(403));
        assertThrows(WebApplicationException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirIngenExceptionsDersomKun200OK() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(true, false), MediaType.JSON_UTF_8).withStatusCode(200));

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    @Test
    public void testAtGirIngenExceptionsDersomKun200MedKanReaktiveresNull() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(null, null), MediaType.JSON_UTF_8).withStatusCode(200));

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(ident));
    }

    private void mockUnderOppfolgingApi() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging").withQueryStringParameter("fnr", ident))
                .respond(response().withBody(settOppfolgingOgReaktivering(true, false)).withStatusCode(200));
    }

    private void mockIkkeUnderOppfolgingApi() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging").withQueryStringParameter("fnr", ident))
                .respond(response().withBody(settOppfolgingOgReaktivering(false, false), MediaType.JSON_UTF_8).withStatusCode(200));
    }

    private String settOppfolgingOgReaktivering(Boolean oppfolging, Boolean reaktivering) {
        return "{\"kanReaktiveres\": "+reaktivering+", \"underOppfolging\": "+oppfolging+"}";
    }


    private String okRegistreringBody() {
        return "{\n" +
                "\"Status\": \"STATUS_SUKSESS\"\n" +
                "}";
    }
}
