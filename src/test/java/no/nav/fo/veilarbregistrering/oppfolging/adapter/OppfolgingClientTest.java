package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import com.google.common.net.MediaType;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import no.nav.fo.veilarbregistrering.config.GammelSystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;

import java.time.LocalDateTime;

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

    private static final Foedselsnummer IDENT = Foedselsnummer.of("10108000398"); //Aremark fiktivt fnr.";
    private static final Bruker BRUKER = Bruker.of(IDENT, AktorId.valueOf("AKTØRID"));

    private BrukerRegistreringRepository brukerRegistreringRepository;
    private BrukerRegistreringService brukerRegistreringService;
    private OppfolgingClient oppfolgingClient;
    private StartRegistreringUtils startRegistreringUtils;
    private ClientAndServer mockServer;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeEach
    public void setup() {

        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
        UnleashService unleashService = mock(UnleashService.class);
        oppfolgingClient = buildClient();
        PersonGateway personGateway = mock(PersonGateway.class);

        brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);
        ProfileringRepository profileringRepository = mock(ProfileringRepository.class);
        ArbeidsforholdGateway arbeidsforholdGateway = mock(ArbeidsforholdGateway.class);
        SykmeldtInfoClient sykeforloepMetadataClient = mock(SykmeldtInfoClient.class);
        startRegistreringUtils = mock(StartRegistreringUtils.class);
        ManuellRegistreringService manuellRegistreringService = mock(ManuellRegistreringService.class);
        ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer = (aktorId, brukersSituasjon, opprettetDato) -> {
        };
        ArbeidssokerProfilertProducer arbeidssokerProfilertProducer = (aktorId, innsatsgruppe, profileringGjennomfort) -> {
        };

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
                        unleashService,
                        arbeidssokerRegistrertProducer,
                        arbeidssokerProfilertProducer);


        when(startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(any(), any())).thenReturn(true);
        when(startRegistreringUtils.profilerBruker(anyInt(), any(), any(), any()))
                .thenReturn(new Profilering()
                        .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                        .setAlder(50)
                        .setJobbetSammenhengendeSeksAvTolvSisteManeder(true));
        when(unleashService.isEnabled("veilarbregistrering.lagreTilstandErAktiv")).thenReturn(true);
        when(unleashService.isEnabled("veilarbregistrering.lagreUtenArenaOverforing")).thenReturn(false);
    }

    private OppfolgingClient buildClient() {
        SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
        GammelSystemUserTokenProvider gammelSystemUserTokenProvider = mock(GammelSystemUserTokenProvider.class);
        Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(any())).thenReturn("");
        when(systemUserTokenProvider.getSystemUserAccessToken()).thenReturn("testToken");
        when(gammelSystemUserTokenProvider.getToken()).thenReturn("testToken");
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT;
        OppfolgingClient oppfolgingClient = this.oppfolgingClient = new OppfolgingClient(baseUrl, httpServletRequestProvider, systemUserTokenProvider, gammelSystemUserTokenProvider);
        return oppfolgingClient;
    }

    @Test
    public void testAtGirRuntimeExceptionDersomOppfolgingIkkeSvarer() {
        mockIkkeUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(404));
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        when (brukerRegistreringRepository.lagre(any(OrdinaerBrukerRegistrering.class), any(Bruker.class))).thenReturn(new OrdinaerBrukerRegistrering());
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, BRUKER));
    }


    @Test
    public void testAtGirInternalErrorExceptionDersomBrukerIkkkeHarTilgangTilOppfolging() {
        mockUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(401));
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, BRUKER));
    }


    @Test
    public void testAtRegistreringGirOKDersomBrukerIkkeHarOppfolgingsflaggOgIkkeSkalReaktiveres() {
        when(brukerRegistreringRepository.lagre(any(), any())).thenReturn(new OrdinaerBrukerRegistrering());
        when(startRegistreringUtils.profilerBruker(anyInt(), any(), any(), any())).thenReturn(lagProfilering());
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(false, false), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(204).withBody(okRegistreringBody(), MediaType.JSON_UTF_8));

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        assertNotNull(brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, BRUKER));
    }

    @Test
    public void testAtReaktiveringFeilerDersomArenaSierAtBrukerErUnderOppfolging() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(true, false), MediaType.JSON_UTF_8).withStatusCode(200));

        assertThrows(RuntimeException.class, () -> brukerRegistreringService.reaktiverBruker(BRUKER));
    }

    @Test
    public void testAtReaktiveringGirOKDersomArenaSierAtBrukerKanReaktiveres() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(false, true), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/reaktiverbruker")).respond(response().withStatusCode(204));

        brukerRegistreringService.reaktiverBruker(BRUKER);
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomAktiverBrukerFeiler() {
        mockUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(502));
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, BRUKER));
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomBrukerIkkkeHarTilgangTilOppfolgingStatus() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(401));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(IDENT));
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomOppfolgingFeiler() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(500));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(IDENT));
    }

    @Test
    public void testAtGirWebApplicationExceptionExceptionDersomIngenTilgang() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(403));
        assertThrows(WebApplicationException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(IDENT));
    }

    @Test
    public void bruker_mangler_oppholdstillatelse_eller_arbeidstillatelse() {
        mockUnderOppfolgingApi();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withBody("").withStatusCode(403));
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        assertThrows(WebApplicationException.class, () -> brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, BRUKER));
    }

    @Test
    public void testAtGirIngenExceptionsDersomKun200OK() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(true, false), MediaType.JSON_UTF_8).withStatusCode(200));

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(IDENT));
    }

    @Test
    public void testAtGirIngenExceptionsDersomKun200MedKanReaktiveresNull() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(null, null), MediaType.JSON_UTF_8).withStatusCode(200));

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(IDENT));
    }

    private void mockUnderOppfolgingApi() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging").withQueryStringParameter("fnr", IDENT.stringValue()))
                .respond(response().withBody(settOppfolgingOgReaktivering(true, false)).withStatusCode(200));
    }

    private void mockIkkeUnderOppfolgingApi() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging").withQueryStringParameter("fnr", IDENT.stringValue()))
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
