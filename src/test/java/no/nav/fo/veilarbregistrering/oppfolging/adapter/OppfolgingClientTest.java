package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import com.google.common.net.MediaType;
import no.nav.common.featuretoggle.UnleashService;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.FileToJson;
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.metrics.MetricsService;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
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
    private static final int MOCKSERVER_PORT = 1084;

    private static final Foedselsnummer IDENT = Foedselsnummer.of("10108000398"); //Aremark fiktivt fnr.";
    private static final Fnr FNR = new Fnr("10108000398"); //Aremark fiktivt fnr.";
    private static final Bruker BRUKER = Bruker.of(IDENT, AktorId.of("AKTØRID"));

    private InaktivBrukerService inaktivBrukerService;
    private OppfolgingClient oppfolgingClient;
    private ClientAndServer mockServer;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeEach
    public void setup() {

        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
        oppfolgingClient = buildClient();
        BrukerRegistreringRepository brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);
        SykmeldtInfoClient sykeforloepMetadataClient = mock(SykmeldtInfoClient.class);
        ProfileringService profileringService = mock(ProfileringService.class);
        UnleashService unleashService = mock(UnleashService.class);
        AutorisasjonService autorisasjonService = mock(AutorisasjonService.class);
        MetricsService metricsService = mock(MetricsService.class);

        OppfolgingGatewayImpl oppfolgingGateway = new OppfolgingGatewayImpl(oppfolgingClient);

        BrukerTilstandService brukerTilstandService = new BrukerTilstandService(
                oppfolgingGateway,
                new SykemeldingService(new SykemeldingGatewayImpl(sykeforloepMetadataClient), autorisasjonService, metricsService), unleashService);

        inaktivBrukerService = new InaktivBrukerService(
                brukerTilstandService,
                brukerRegistreringRepository,
                oppfolgingGateway);

        when(profileringService.profilerBruker(anyInt(), any(), any()))
                .thenReturn(new Profilering()
                        .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                        .setAlder(50)
                        .setJobbetSammenhengendeSeksAvTolvSisteManeder(true));
    }

    private OppfolgingClient buildClient() {
        SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
        Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(any())).thenReturn("");
        when(systemUserTokenProvider.getSystemUserToken()).thenReturn("testToken");
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT;
        UnleashService unleashService = mock(UnleashService.class);
        when(unleashService.isEnabled(any())).thenReturn(false);
        return this.oppfolgingClient = new OppfolgingClient(baseUrl, systemUserTokenProvider);
    }

    @Test
    public void skal_kaste_RuntimeException_ved_vilkaarlig_feil_som_ikke_er_haandtert() {
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(404));
        assertThrows(RuntimeException.class, () -> oppfolgingClient.aktiverBruker(new AktiverBrukerData(FNR, Innsatsgruppe.SITUASJONSBESTEMT_INNSATS)));
    }

    @Test
    public void skal_returnere_response_ved_204() {
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withStatusCode(204).withBody(okRegistreringBody(), MediaType.JSON_UTF_8));

        assertNotNull(oppfolgingClient.aktiverBruker(new AktiverBrukerData(FNR, Innsatsgruppe.SITUASJONSBESTEMT_INNSATS)));
    }

    @Test
    public void testAtReaktiveringFeilerDersomArenaSierAtBrukerErUnderOppfolging() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(true, false), MediaType.JSON_UTF_8).withStatusCode(200));

        assertThrows(RuntimeException.class, () -> inaktivBrukerService.reaktiverBruker(BRUKER));
    }

    @Test
    public void testAtReaktiveringGirOKDersomArenaSierAtBrukerKanReaktiveres() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(false, true), MediaType.JSON_UTF_8).withStatusCode(200));
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/reaktiverbruker")).respond(response().withStatusCode(204));

        inaktivBrukerService.reaktiverBruker(BRUKER);
    }

    //FIXME: Vurder å flytte disse testene til en ny testklasse med mindre kompleksitet hvor vi tester Client eller
    // Gateway, men ikke alt annet samtidig.
    /*
    @Test
    public void testAtGirInternalServerErrorExceptionDersomBrukerIkkkeHarTilgangTilOppfolgingStatus() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(401));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(BRUKER, null));
    }

    @Test
    public void testAtGirInternalServerErrorExceptionDersomOppfolgingFeiler() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(500));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(BRUKER, null));
    }

    @Test
    public void testAtGirWebApplicationExceptionExceptionDersomIngenTilgang() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(403));
        assertThrows(WebApplicationException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(BRUKER, null));
    }
*/
    @Test
    public void skaL_returnere_ved_manglende_oppholdstillatelse() {
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(response().withBody(FileToJson.toJson("/oppfolging/manglerOppholdstillatelse.json")).withStatusCode(403));

        AktiverBrukerResultat actual = oppfolgingClient.aktiverBruker(new AktiverBrukerData(FNR, Innsatsgruppe.SITUASJONSBESTEMT_INNSATS));

        assertThat(actual.feil()).isEqualTo(AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE);
    }

    //FIXME: Vurder å flytte disse testene til en ny testklasse med mindre kompleksitet hvor vi tester Client eller
    // Gateway, men ikke alt annet samtidig.
    /*
    @Test
    public void testAtGirIngenExceptionsDersomKun200OK() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(true, false), MediaType.JSON_UTF_8).withStatusCode(200));

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(BRUKER));
    }

    @Test
    public void testAtGirIngenExceptionsDersomKun200MedKanReaktiveresNull() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(null, null), MediaType.JSON_UTF_8).withStatusCode(200));

        when(arbeidsforholdGateway.hentArbeidsforhold(any())).thenReturn(flereArbeidsforholdTilfeldigSortert());

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(BRUKER));
    }
*/

    private String settOppfolgingOgReaktivering(Boolean oppfolging, Boolean reaktivering) {
        return "{\"kanReaktiveres\": "+reaktivering+", \"underOppfolging\": "+oppfolging+"}";
    }

    private String okRegistreringBody() {
        return "{\n" +
                "\"Status\": \"STATUS_SUKSESS\"\n" +
                "}";
    }
}
