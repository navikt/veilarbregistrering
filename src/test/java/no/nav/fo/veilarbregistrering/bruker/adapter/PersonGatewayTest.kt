package no.nav.fo.veilarbregistrering.bruker.adapter

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.finn.unleash.UnleashContext
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.health.HealthCheckResult
import no.nav.fo.veilarbregistrering.bruker.*
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
import java.util.*
import javax.servlet.http.HttpServletRequest

@ExtendWith(MockServerExtension::class)
class PersonGatewayTest(private val mockServer: ClientAndServer) {
    private lateinit var personGateway: PersonGateway

    @BeforeEach
    fun setup() {
        val unleashClient = StubUnleashClient(listOf("veilarbregistrering.geografiskTilknytningFraPdl.sammenligning"))
        personGateway = PersonGatewayImpl(lagVeilArbPersonClient(), lagPdlOppslagGateway(), unleashClient)
    }

    private fun lagPdlOppslagGateway(gt: GeografiskTilknytning? = null): PdlOppslagGateway {
        val pdlOppslagGatewayMock = mockk<PdlOppslagGateway>()
        every { pdlOppslagGatewayMock.hentGeografiskTilknytning(any())} returns Optional.ofNullable(gt)
        return pdlOppslagGatewayMock
    }

    private fun lagVeilArbPersonClient(): VeilArbPersonClient {
        val httpServletRequest = mockk<HttpServletRequest>()
        mockkStatic(RequestContext::class)
        every { servletRequest() } returns httpServletRequest
        every { httpServletRequest.getHeader(any()) } returns ""
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return VeilArbPersonClient(baseUrl)
    }

    @Test
    fun `hentGeografiskTilknytning skal returnere kontorid`() {
        val foedselsnummer = Foedselsnummer.of("12345678910")
        val bruker = Bruker.of(foedselsnummer, null)
        val forventetGeografiskTilknytning = "1234"

        konfigurerVeilarbpersonClient(foedselsnummer, forventetGeografiskTilknytning)

        val geografiskTilknytning = personGateway.hentGeografiskTilknytning(bruker)
        Assertions.assertThat(geografiskTilknytning).hasValue(GeografiskTilknytning.of(forventetGeografiskTilknytning))
    }

    private fun konfigurerVeilarbpersonClient(
        foedselsnummer: Foedselsnummer,
        forventetGeografiskTilknytning: String
    ) {
        mockServer.`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withPath("/person/geografisktilknytning")
                .withQueryStringParameter("fnr", foedselsnummer.stringValue())
        )
            .respond(
                HttpResponse.response()
                    .withBody(
                        "{\"geografiskTilknytning\": \"" + forventetGeografiskTilknytning + "\"}",
                        MediaType.JSON_UTF_8
                    )
                    .withStatusCode(200)
            )
    }

    @Test
    fun `hentGeografiskTilknytning skal returnere optional hvis 404`() {
        val foedselsnummer = Foedselsnummer.of("12345678911")
        val bruker = Bruker.of(foedselsnummer, null)

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
        val geografiskTilknytning = personGateway.hentGeografiskTilknytning(bruker)
        Assertions.assertThat(geografiskTilknytning).isEmpty
    }

    @Test
    fun `hentGeografiskTilknytning skal returnere optional hvis tom tekst`() {
        val foedselsnummer = Foedselsnummer.of("12345678912")
        val bruker = Bruker.of(foedselsnummer, null)

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
        val geografiskTilknytning = personGateway.hentGeografiskTilknytning(bruker)
        Assertions.assertThat(geografiskTilknytning).isEmpty
    }
}


class StubUnleashClient(private val aktiveFeatures: List<String>) : UnleashClient {
    override fun checkHealth() = HealthCheckResult.healthy()!!
    override fun isEnabled(feature: String) = aktiveFeatures.contains(feature)
    override fun isEnabled(feature: String, context: UnleashContext) = isEnabled(feature)
}
