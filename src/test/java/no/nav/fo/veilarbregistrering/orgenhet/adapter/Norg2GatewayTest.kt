package no.nav.fo.veilarbregistrering.orgenhet.adapter

import com.google.common.net.MediaType
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr.Companion.of
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse

class Norg2GatewayTest {

    private lateinit var mockServer: ClientAndServer

    @AfterEach
    fun tearDown() {
        mockServer.stop()
    }

    @BeforeEach
    fun setup() {
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT)
    }

    private fun buildClient(): Norg2RestClient {
        val baseUrl = "http://$MOCKSERVER_URL:$MOCKSERVER_PORT"
        return Norg2RestClient(baseUrl)
    }

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun skal_hente_enhetsnr_fra_norg2_for_kommunenummer() {
        val norg2Gateway = Norg2GatewayImpl(buildClient())

        val json = FileToJson.toJson("/orgenhet/orgenhet.json")

        mockServer.`when`(
                HttpRequest
                        .request()
                        .withMethod("POST")
                        .withPath("/v1/arbeidsfordeling/enheter/bestmatch"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(json, MediaType.JSON_UTF_8))

        val enhetsnr = norg2Gateway.hentEnhetFor(Kommunenummer.of("0302"))

        Assertions.assertThat(enhetsnr).isNotEmpty
        Assertions.assertThat(enhetsnr).hasValue(of("0393"))
    }

    companion object {
        private const val MOCKSERVER_URL = "localhost"
        private const val MOCKSERVER_PORT = 1083
    }
}