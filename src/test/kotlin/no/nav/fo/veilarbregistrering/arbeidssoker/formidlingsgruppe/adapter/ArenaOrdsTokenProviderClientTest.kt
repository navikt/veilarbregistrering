package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.adapter

import no.nav.fo.veilarbregistrering.FileToJson
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

@ExtendWith(MockServerExtension::class)
class ArenaOrdsTokenProviderClientTest(private val mockServer: ClientAndServer) {

    @BeforeEach
    fun setup() {
        println(mockServer)
        mockServer.reset()
        System.setProperty("ARENA_ORDS_CLIENT_ID", "1")
        System.setProperty("ARENA_ORDS_CLIENT_SECRET", "1")
        System.setProperty("NAIS_CLUSTER_NAME", "dev-gcp")
    }

    @Test
    fun `Should get a token`() {
        val client = buildClient()

        mockServer.`when`(
            HttpRequest.request()
                .withMethod("POST")
                .withHeader("Authorization", mockAuth)
                .withHeader("Downstream-Authorization", mockBasicAuth)
                .withContentType(MediaType.APPLICATION_FORM_URLENCODED.withCharset("utf-8"))
                .withBody("grant_type=client_credentials")
        ).respond(
            HttpResponse.response()
            .withStatusCode(200)
            .withBody(FileToJson.toJson("/arbeidsforhold/ordstoken.json"), MediaType.JSON_UTF_8)
        )

        val token = client.token
        Assertions.assertThat(token).isNotNull
    }


    private fun buildClient(): ArenaOrdsTokenProviderClient = mockServer.remoteAddress().let {
        ArenaOrdsTokenProviderClient("http://${it.hostName}:${it.port}") { "testToken" }
    }
}

private val mockBasicAuth: String = "Basic ${Base64.getEncoder().encodeToString("1:1".toByteArray())}"
private const val mockAuth = "Bearer testToken"
