package no.nav.fo.veilarbregistrering.sykemelding.adapter

import com.google.common.net.MediaType
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.featuretoggle.UnleashService
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerTilstandService
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringService
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import org.junit.jupiter.api.*
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import javax.inject.Provider
import javax.servlet.http.HttpServletRequest

internal class SykmeldtInfoClientTest {
    private lateinit var sykmeldtRegistreringService: SykmeldtRegistreringService
    private lateinit var oppfolgingClient: OppfolgingClient
    private lateinit var sykeforloepMetadataClient: SykmeldtInfoClient
    private lateinit var mockServer: ClientAndServer
    @AfterEach
    fun tearDown() {
        mockServer.stop()
    }

    @BeforeEach
    fun setup() {
        val brukerRegistreringRepository: BrukerRegistreringRepository = mockk()
        val manuellRegistreringRepository: ManuellRegistreringRepository = mockk()
        val unleashService: UnleashService = mockk(relaxed = true)
        val metricsService: MetricsService = mockk(relaxed = true)
        val autorisasjonService: AutorisasjonService = mockk(relaxed = true)
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT)
        oppfolgingClient = buildOppfolgingClient()
        sykeforloepMetadataClient = buildSykeForloepClient()
        val oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient)
        sykmeldtRegistreringService = SykmeldtRegistreringService(
            BrukerTilstandService(
                oppfolgingGateway,
                SykemeldingService(
                    SykemeldingGatewayImpl(sykeforloepMetadataClient),
                    autorisasjonService,
                    metricsService
                ),
                unleashService
            ),
            oppfolgingGateway,
            brukerRegistreringRepository,
            manuellRegistreringRepository,
            metricsService
        )
    }

    private fun buildSykeForloepClient(): SykmeldtInfoClient {
        ConfigBuildClient()()
        val baseUrl = "http://$MOCKSERVER_URL:$MOCKSERVER_PORT/"
        return SykmeldtInfoClient(baseUrl)
    }

    private fun buildOppfolgingClient(): OppfolgingClient {
        ConfigBuildClient()()
        val baseUrl = "http://$MOCKSERVER_URL:$MOCKSERVER_PORT"
        return OppfolgingClient(baseUrl, null)
    }

    @Test
    @Disabled
    fun testAtRegistreringAvSykmeldtGirOk() {
        mockSykmeldtIArena()
        mockSykmeldtOver39u()
        val sykmeldtRegistrering = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering()
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverSykmeldt")).respond(
            HttpResponse.response().withStatusCode(204)
        )
        sykmeldtRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER, null)
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
    fun testAtGirInternalServerErrorExceptionDersomRegistreringAvSykmeldtFeiler() {
        mockSykmeldtIArena()
        mockSykmeldtOver39u()
        val sykmeldtRegistrering = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering()
        mockServer
            .`when`(
                HttpRequest.request()
                    .withMethod("POST")
                    .withPath("/oppfolging/aktiverSykmeldt")
            )
            .respond(
                HttpResponse.response()
                    .withStatusCode(502)
            )
        Assertions.assertThrows(RuntimeException::class.java) {
            sykmeldtRegistreringService.registrerSykmeldt(
                sykmeldtRegistrering,
                BRUKER,
                null
            )
        }
    }

    private fun mockSykmeldtOver39u() {
        mockServer
            .`when`(
                HttpRequest.request()
                    .withMethod("GET")
                    .withPath("/sykeforloep/metadata")
            )
            .respond(
                HttpResponse.response()
                    .withBody(sykmeldtOver39u(), MediaType.JSON_UTF_8)
                    .withStatusCode(200)
            )
    }

    private fun mockSykmeldtIArena() {
        mockServer
            .`when`(
                HttpRequest.request()
                    .withMethod("GET")
                    .withPath("/oppfolging")
            )
            .respond(
                HttpResponse.response()
                    .withBody(harIkkeOppfolgingsflaggOgErInaktivIArenaBody(), MediaType.JSON_UTF_8)
                    .withStatusCode(200)
            )
    }

    private fun sykmeldtOver39u(): String {
        return """
            {
            "erArbeidsrettetOppfolgingSykmeldtInngangAktiv": true
            }
            """.trimIndent()
    }

    private fun harIkkeOppfolgingsflaggOgErInaktivIArenaBody(): String {
        return """
            {
            "erSykmeldtMedArbeidsgiver": true
            }
            """.trimIndent()
    }

    private class ConfigBuildClient {
        operator fun invoke(): Provider<HttpServletRequest> {
            val systemUserTokenProvider: SystemUserTokenProvider = mockk()
            val httpServletRequestProvider: Provider<HttpServletRequest> = mockk()
            val httpServletRequest: HttpServletRequest = mockk()

            mockkStatic(RequestContext::class)
            every { servletRequest() } returns httpServletRequest

            every { httpServletRequest.getHeader(any()) } returns ""
            every { systemUserTokenProvider.systemUserToken } returns "testToken"
            return httpServletRequestProvider
        }
    }

    companion object {
        private const val MOCKSERVER_URL = "localhost"
        private const val MOCKSERVER_PORT = 1083
        private const val IDENT = "10108000398" //Aremark fiktivt fnr.";;
        private val BRUKER = Bruker.of(Foedselsnummer.of(IDENT), AktorId.of("AKTØRID"))
    }
}