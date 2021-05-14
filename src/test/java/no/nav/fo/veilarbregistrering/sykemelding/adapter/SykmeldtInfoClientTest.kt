package no.nav.fo.veilarbregistrering.sykemelding.adapter

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.HttpHeaders

@ExtendWith(MockServerExtension::class)
internal class SykmeldtInfoClientTest(private val mockServer: ClientAndServer) {
    private lateinit var sykeforloepMetadataClient: SykmeldtInfoClient
    private lateinit var sykmeldingGateway: SykemeldingGateway

    @BeforeEach
    fun setup() {
        sykeforloepMetadataClient = buildSykeForloepClient()
        staticMocks()
        sykmeldingGateway = SykemeldingGatewayImpl(sykeforloepMetadataClient)
    }

    private fun buildSykeForloepClient(): SykmeldtInfoClient {
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return SykmeldtInfoClient(baseUrl)
    }

    @Test
    fun `hentSykmeldtInfoData feiler ikke ved ikke-ASCII tegn i Cookies `() {
        val fnr = "11111111111"
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/hentMaksdato")
            .withQueryStringParameter("fnr", fnr)).respond(
                HttpResponse.response().withStatusCode(200).withBody(sykmeldtInfoResponse)
            )
        sykmeldingGateway.hentReberegnetMaksdato(Foedselsnummer.of(fnr))

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
        private const val sykmeldtInfoResponse = "{}"
    }
}