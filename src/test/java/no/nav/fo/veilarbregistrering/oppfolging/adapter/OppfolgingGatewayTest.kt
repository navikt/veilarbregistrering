package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient
import org.junit.jupiter.api.*
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import javax.servlet.http.HttpServletRequest

internal class OppfolgingGatewayTest {
    private lateinit var oppfolgingGateway: OppfolgingGateway
    private lateinit var oppfolgingClient: OppfolgingClient
    private lateinit var sykeforloepMetadataClient: SykmeldtInfoClient
    private lateinit var mockServer: ClientAndServer
    @AfterEach
    fun tearDown() {
        mockServer.stop()
    }

    @BeforeEach
    fun setup() {
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT)
        oppfolgingClient = buildOppfolgingClient()
        sykeforloepMetadataClient = buildSykeForloepClient()
        oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient)
    }

    private fun buildSykeForloepClient(): SykmeldtInfoClient {
        val baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT + "/"
        return SykmeldtInfoClient(baseUrl).also { sykeforloepMetadataClient = it }
    }

    private fun buildOppfolgingClient(): OppfolgingClient {
        val httpServletRequest: HttpServletRequest = mockk()
        mockkStatic(RequestContext::class)
        every { RequestContext.servletRequest() } returns httpServletRequest
        val baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT
        return OppfolgingClient(mockk(relaxed = true), jacksonObjectMapper().findAndRegisterModules(), baseUrl, mockk(relaxed = true)).also { oppfolgingClient = it }
    }

    @Test
    @Disabled
    fun testAtRegistreringAvSykmeldtGirOk() {
        val sykmeldtRegistrering = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering()
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverSykmeldt")).respond(
            HttpResponse.response().withStatusCode(204)
        )
        oppfolgingGateway.settOppfolgingSykmeldt(BRUKER.gjeldendeFoedselsnummer, sykmeldtRegistrering.besvarelse)
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
            oppfolgingGateway.settOppfolgingSykmeldt(
                BRUKER.gjeldendeFoedselsnummer,
                sykmeldtRegistrering.besvarelse
            )
        }
    }

    companion object {
        private const val MOCKSERVER_URL = "localhost"
        private const val MOCKSERVER_PORT = 1082
        private const val IDENT = "10108000398" //Aremark fiktivt fnr.";;
        private val BRUKER = Bruker.of(Foedselsnummer.of(IDENT), AktorId.of("AKTØRID"))
    }
}