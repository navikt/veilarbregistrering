package no.nav.fo.veilarbregistrering.sykemelding.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.HttpHeaders

@ExtendWith(MockServerExtension::class)
internal class SykmeldtInfoClientTest(private val mockServer: ClientAndServer) {
    private lateinit var sykmeldtRegistreringService: SykmeldtRegistreringService
    private lateinit var oppfolgingClient: OppfolgingClient
    private lateinit var sykeforloepMetadataClient: SykmeldtInfoClient
    private lateinit var sykmeldingGateway: SykemeldingGateway

    @BeforeEach
    fun setup() {
        val brukerRegistreringRepository: BrukerRegistreringRepository = mockk()
        val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository = mockk()
        val manuellRegistreringRepository: ManuellRegistreringRepository = mockk()
        val unleashClient: UnleashClient = mockk(relaxed = true)
        val influxMetricsService: InfluxMetricsService = mockk(relaxed = true)
        val autorisasjonService: AutorisasjonService = mockk(relaxed = true)
        oppfolgingClient = buildOppfolgingClient(influxMetricsService, jacksonObjectMapper().findAndRegisterModules())
        sykeforloepMetadataClient = buildSykeForloepClient()
        staticMocks()
        val oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient)
        sykmeldingGateway = SykemeldingGatewayImpl(sykeforloepMetadataClient)
        sykmeldtRegistreringService = SykmeldtRegistreringService(
            BrukerTilstandService(
                    oppfolgingGateway,
                    SykemeldingService(
                        sykmeldingGateway,
                        autorisasjonService,
                        influxMetricsService
                    ),
                    brukerRegistreringRepository
            ),
            oppfolgingGateway,
            sykmeldtRegistreringRepository,
            manuellRegistreringRepository,
            influxMetricsService
        )
    }

    private fun buildSykeForloepClient(): SykmeldtInfoClient {
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return SykmeldtInfoClient(baseUrl)
    }

    private fun buildOppfolgingClient(metricsClient: InfluxMetricsService, objectMapper: ObjectMapper): OppfolgingClient {
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        val systemUserTokenProvider: SystemUserTokenProvider = mockk()
        every { systemUserTokenProvider.systemUserToken } returns "testToken"
        return OppfolgingClient(objectMapper, metricsClient, baseUrl, systemUserTokenProvider)
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
    fun `hentSykmeldtInfoData feiler ikke ved ikke-ASCII tegn i Cookies `() {
        val fnr = "11111111111"
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/hentMaksdato")
            .withQueryStringParameter("fnr", fnr)).respond(
                HttpResponse.response().withStatusCode(200).withBody(sykmeldtInfoResponse)
            )
        sykmeldingGateway.hentReberegnetMaksdato(Foedselsnummer.of(fnr))

    }

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


    companion object {
        fun staticMocks() {
            val httpServletRequest: HttpServletRequest = mockk()
            mockkStatic(RequestContext::class)
            every { servletRequest() } returns httpServletRequest
            every { httpServletRequest.getHeader(HttpHeaders.COOKIE) } returns "czas Å\u009Brodkowoeuropejski standardowy"
            every { httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION) } returns "testToken"
            every { httpServletRequest.cookies } returns emptyArray()
        }
        private const val IDENT = "10108000398" //Aremark fiktivt fnr.";;
        private val BRUKER = Bruker.of(Foedselsnummer.of(IDENT), AktorId.of("AKTØRID"))
        private const val sykmeldtInfoResponse = "{}"
    }
}