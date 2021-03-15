package no.nav.fo.veilarbregistrering.bruker.adapter

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.HttpHeaders

@ExtendWith(MockServerExtension::class)
internal class VeilArbPersonClientTest(private val mockServer: ClientAndServer) {
    private lateinit var veilArbPersonClient: VeilArbPersonClient

    @BeforeEach
    fun setup() {
        mockServer.reset()
        veilArbPersonClient = buildClient()
    }

    private fun buildClient(): VeilArbPersonClient {
        mockkStatic(RequestContext::class)
        val systemUserTokenProvider: SystemUserTokenProvider = mockk()
        val httpServletRequest: HttpServletRequest = mockk()
        every { RequestContext.servletRequest() } returns httpServletRequest
        every { httpServletRequest.getHeader(HttpHeaders.COOKIE) } returns "czas Å\u009Brodkowoeuropejski standardowy"
        every { systemUserTokenProvider.systemUserToken } returns "testToken"
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return VeilArbPersonClient(baseUrl, systemUserTokenProvider)
    }

    @Test
    fun `Returnerer geografisk tilknytting`() {
        mockServer.`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withPath(geografiskTilnytningPath)
                .withQueryStringParameter("fnr", norskFnr)
        ).respond(
            HttpResponse.response().withStatusCode(200)
                .withBody(geografiskTilknytningResponse, MediaType.APPLICATION_JSON)
        )
        val expected = GeografiskTilknytningDto().also { it.geografiskTilknytning = "Norge" }
        assertThat(
            veilArbPersonClient.geografisktilknytning(Foedselsnummer.of(norskFnr)).get().geografiskTilknytning
        ).isEqualTo(expected.geografiskTilknytning)
    }

    @Test
    fun `Returnerer tomt objekt ved 404`() {
        mockServer.`when`(HttpRequest.request()
            .withMethod("GET")
            .withPath(geografiskTilnytningPath)
            .withQueryStringParameter("fnr", ukjentFnr)
        ).respond(
            HttpResponse.response().withStatusCode(404)
        )

        assertThat(veilArbPersonClient.geografisktilknytning(Foedselsnummer.of(ukjentFnr))).isEmpty
    }

    companion object {
        private const val geografiskTilnytningPath = "/person/geografisktilknytning"
        private const val ukjentFnr = "11111111111"
        private const val norskFnr = "20109911111"
        private val geografiskTilknytningResponse = """{
            "geografiskTilknytning": "Norge"
             }
        """.trimIndent()
    }
}