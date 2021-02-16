package no.nav.fo.veilarbregistrering.bruker.adapter

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning
import no.nav.fo.veilarbregistrering.bruker.PersonGateway
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import javax.servlet.http.HttpServletRequest

@ExtendWith(MockServerExtension::class)
class PersonGatewayTest(private val mockServer: ClientAndServer) {
    private lateinit var veilArbPersonClient: VeilArbPersonClient
    private lateinit var personGateway: PersonGateway

    @BeforeEach
    fun setup() {
        veilArbPersonClient = buildClient()
        personGateway = PersonGatewayImpl(veilArbPersonClient)
    }

    private fun buildClient(): VeilArbPersonClient {
        val systemUserTokenProvider = mockk<SystemUserTokenProvider>()
        val httpServletRequest = mockk<HttpServletRequest>()
        mockkStatic(RequestContext::class)
        every { servletRequest() } returns httpServletRequest
        every { httpServletRequest.getHeader(any()) } returns ""
        every { systemUserTokenProvider.systemUserToken } returns "testToken"
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return VeilArbPersonClient(baseUrl, systemUserTokenProvider).also { veilArbPersonClient = it }
    }

    @Test
    fun hentGeografiskTilknytning_skal_returnere_kontorid() {
        val foedselsnummer = Foedselsnummer.of("12345678910")

        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/person/geografisktilknytning")
                        .withQueryStringParameter("fnr", foedselsnummer.stringValue())
        )
            .respond(
                    HttpResponse.response()
                            .withBody("{\"geografiskTilknytning\": " + "1234" + "}", MediaType.JSON_UTF_8)
                            .withStatusCode(200)
            )
        val geografiskTilknytning = personGateway.hentGeografiskTilknytning(foedselsnummer)
        Assertions.assertThat(geografiskTilknytning).hasValue(GeografiskTilknytning.of("1234"))
    }

    @Test
    fun hentGeografiskTilknytning_skal_returnere_optional_hvis_404() {
        val foedselsnummer = Foedselsnummer.of("12345678911")
        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/person/geografisktilknytning")
                        .withQueryStringParameter("fnr", foedselsnummer.stringValue())
        )
            .respond(
                    HttpResponse.response()
                            .withStatusCode(404)
            )
        val geografiskTilknytning = personGateway.hentGeografiskTilknytning(foedselsnummer)
        Assertions.assertThat(geografiskTilknytning).isEmpty
    }

    @Test
    fun hentGeografiskTilknytning_skal_returnere_optional_hvis_tom_tekst() {
        val foedselsnummer = Foedselsnummer.of("12345678912")
        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/person/geografisktilknytning")
                        .withQueryStringParameter("fnr", foedselsnummer.stringValue())
        )
            .respond(
                    HttpResponse.response()
                            .withBody("{\"geografiskTilknytning\": " + "null" + "}", MediaType.JSON_UTF_8)
                            .withStatusCode(200)
            )
        val geografiskTilknytning = personGateway.hentGeografiskTilknytning(foedselsnummer)
        Assertions.assertThat(geografiskTilknytning).isEmpty
    }
}