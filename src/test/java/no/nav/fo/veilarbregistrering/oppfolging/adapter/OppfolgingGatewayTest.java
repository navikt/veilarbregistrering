package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class OppfolgingGatewayTest {
    private static final String MOCKSERVER_URL = "localhost";
    private static final int MOCKSERVER_PORT = 1082;

    private static final String IDENT = "10108000398"; //Aremark fiktivt fnr.";;
    private static final Bruker BRUKER = Bruker.of(Foedselsnummer.of(IDENT), AktorId.of("AKTØRID"));

    private OppfolgingGateway oppfolgingGateway;

    private OppfolgingClient oppfolgingClient;
    private SykmeldtInfoClient sykeforloepMetadataClient;
    private ClientAndServer mockServer;

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeEach
    public void setup() {
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
        oppfolgingClient = buildOppfolgingClient();
        sykeforloepMetadataClient = buildSykeForloepClient();

        oppfolgingGateway = new OppfolgingGatewayImpl(oppfolgingClient);
    }

    private SykmeldtInfoClient buildSykeForloepClient() {
        Provider<HttpServletRequest> httpServletRequestProvider = new ConfigBuildClient().invoke();
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT + "/";
        return sykeforloepMetadataClient = new SykmeldtInfoClient(baseUrl, httpServletRequestProvider);
    }

    private OppfolgingClient buildOppfolgingClient() {
        Provider<HttpServletRequest> httpServletRequestProvider = new ConfigBuildClient().invoke();
        String baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT;
        return oppfolgingClient = new OppfolgingClient(baseUrl, httpServletRequestProvider, null, null, null);
    }

    @Test
    @Disabled
    public void testAtRegistreringAvSykmeldtGirOk() {
        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();
        mockServer.when(request().withMethod("POST").withPath("/oppfolging/aktiverSykmeldt")).respond(response().withStatusCode(204));
        oppfolgingGateway.settOppfolgingSykmeldt(BRUKER.getGjeldendeFoedselsnummer(), sykmeldtRegistrering.getBesvarelse());
    }

    /*
        @Test
        @Disabled
        public void testAtHentingAvSykeforloepMetadataGirOk() {
            mockSykmeldtIArena();
            mockSykmeldtOver39u();
            StartRegistreringStatusDto startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(BRUKER);
            assertSame(startRegistreringStatus.getRegistreringType(), SYKMELDT_REGISTRERING);
        }
    */
    @Test
    public void testAtGirInternalServerErrorExceptionDersomRegistreringAvSykmeldtFeiler() {
        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();
        mockServer
                .when(request()
                        .withMethod("POST")
                        .withPath("/oppfolging/aktiverSykmeldt"))
                .respond(response()
                        .withStatusCode(502));
        assertThrows(RuntimeException.class, () -> oppfolgingGateway.settOppfolgingSykmeldt(BRUKER.getGjeldendeFoedselsnummer(), sykmeldtRegistrering.getBesvarelse()));
    }

    private static class ConfigBuildClient {
        public Provider<HttpServletRequest> invoke() {
            SystemUserTokenProvider systemUserTokenProvider = mock(SystemUserTokenProvider.class);
            Provider<HttpServletRequest> httpServletRequestProvider = mock(Provider.class);
            HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
            when(httpServletRequestProvider.get()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader(any())).thenReturn("");
            when(systemUserTokenProvider.getSystemUserAccessToken()).thenReturn("testToken");
            return httpServletRequestProvider;
        }
    }
}
