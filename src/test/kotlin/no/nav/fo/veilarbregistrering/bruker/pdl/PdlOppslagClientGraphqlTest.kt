package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType

@ExtendWith(MockServerExtension::class)
class PdlOppslagClientGraphqlTest(private val mockServer: ClientAndServer) {
    private val authToken = "AuthToken"

    @BeforeEach
    fun setup() {
        mockServer.reset()
    }

    @Test
    fun `hentPerson skal lage en gyldig graphql-request`() {
        val fnr = "12345678910"
        val client = buildClient()

        mockServer.`when`(
            HttpRequest.request("/graphql")
                .withMethod("POST")
                .withHeader("Authorization", "Bearer $authToken")
                .withHeader("Tema", "OPP")
                .withHeader("Nav-Personident", fnr)
                .withContentType(MediaType.JSON_UTF_8)
        ).respond(response()
            .withStatusCode(200)
            .withBody(toJson(PdlOppslagClientTest.HENT_PERSON_OK_JSON), MediaType.JSON_UTF_8)
        )

        val pdlPerson = client.hentPerson(AktorId(fnr))
        assertThat(pdlPerson).isNotNull
    }

    @Test
    fun `hentGeografiskTilknytning skal lage en gyldig graphql-request`() {
        val fnr = "12345678910"
        val client = buildClient()

        mockServer.`when`(
            HttpRequest.request("/graphql")
                .withMethod("POST")
                .withHeader("Authorization", "Bearer $authToken")
                .withHeader("Tema", "OPP")
                .withHeader("Nav-Personident", fnr)
                .withContentType(MediaType.JSON_UTF_8)
        ).respond(response()
            .withStatusCode(200)
            .withBody(toJson(PdlOppslagClientTest.HENT_GEOGRAFISK_TILKNYTNING_OK_JSON), MediaType.JSON_UTF_8)
        )

        val pdlGeografiskTilknytning = client.hentGeografiskTilknytning(AktorId(fnr))
        assertThat(pdlGeografiskTilknytning).isNotNull
    }

    @Test
    fun `hentIdenter skal lage en gyldig graphql-request`() {
        val fnr = "12345678910"
        val client = buildClient()

        mockServer.`when`(
            HttpRequest.request("/graphql")
                .withMethod("POST")
                .withHeader("Authorization", "Bearer $authToken")
                .withHeader("Nav-Personident", fnr)
                .withContentType(MediaType.JSON_UTF_8)
        ).respond(response()
            .withStatusCode(200)
            .withBody(toJson(PdlOppslagClientTest.HENT_IDENTER_OK_JSON), MediaType.JSON_UTF_8)
        )

        val pdlIdenter = client.hentIdenter(Foedselsnummer(fnr))
        assertThat(pdlIdenter).isNotNull
    }

    @Test
    fun `hentIdenterBolk skal lage en ny gyldig graphql-request`() {
        val client = buildClient()

        mockServer.`when`(
            HttpRequest.request("/graphql")
                .withMethod("POST")
                .withHeader("Authorization", "Bearer $authToken")
                .withContentType(MediaType.JSON_UTF_8)
        ).respond(response()
            .withStatusCode(200)
            .withBody(toJson(PdlOppslagClientTest.HENT_IDENTER_BOLK_OK_JSON), MediaType.JSON_UTF_8)
        )

        val identerBolk = client.hentIdenterBolk(listOf(AktorId("34211kd"), AktorId("422dasfda")))
        assertThat(identerBolk.size).isEqualTo(2)
    }

    private fun buildClient(): PdlOppslagClient {
        val baseUrl = "http://${mockServer.remoteAddress().address.hostName}:${mockServer.remoteAddress().port}"
        return PdlOppslagClient(baseUrl) { authToken }
    }
}
