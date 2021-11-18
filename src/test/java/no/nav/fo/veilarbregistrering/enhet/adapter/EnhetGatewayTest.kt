package no.nav.fo.veilarbregistrering.enhet.adapter

import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer.Companion.of
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType

@ExtendWith(MockServerExtension::class)
class EnhetGatewayTest(private val mockServer: ClientAndServer) {
    private lateinit var enhetGateway: EnhetGatewayImpl

    @BeforeEach
    fun setUp() {
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        enhetGateway = EnhetGatewayImpl(EnhetRestClient(baseUrl))
    }

    @Test
    fun `hentOrganisasjonsdetaljer skal kunne hente ut kommunenummer fra enhetsregisteret`() {
        val org = of("995298775")
        mockServer.`when`(
            HttpRequest
                .request()
                .withMethod("GET")
                .withPath("/api/v1/organisasjon/${org.asString()}"))
            .respond(
                HttpResponse.response()
                .withStatusCode(200)
                .withBody(OK_JSON, MediaType.JSON_UTF_8))

        val organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(org)
        assertThat(organisasjonsdetaljer).isNotNull
        assertThat(organisasjonsdetaljer!!.kommunenummer()).hasValue(Kommunenummer("0301"))
    }

    @Test
    fun `hentOrganisasjonsdetaljer skal gi empty result ved ukjent org nr`() {
        val ukjentOrg = of("123456789")

        mockServer.`when`(
            HttpRequest
                .request()
                .withMethod("GET")
                .withPath("/api/v1/organisasjon/${ukjentOrg.asString()}"))
            .respond(
                HttpResponse.response()
                    .withStatusCode(404))

        val organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(ukjentOrg)
        assertThat(organisasjonsdetaljer).isNull()
    }

    @Test
    fun `hentOrganisasjonsdetaljer skal kaste runtimeException ved feil`() {
        mockServer.`when`(
            HttpRequest
                .request()
                .withMethod("GET")
                .withPath("/api/v1/organisasjon/${FEILENDE_ORG.asString()}"))
            .respond(
                HttpResponse.response()
                    .withStatusCode(500))

        val runtimeException = assertThrows<RuntimeException> { enhetGateway.hentOrganisasjonsdetaljer(FEILENDE_ORG) }
        assertThat(runtimeException.message).isEqualTo("Hent organisasjon feilet med status: 500")
    }

    companion object {
        private val OK_JSON = FileToJson.toJson("/enhet/enhet.json")
        private val FEILENDE_ORG: Organisasjonsnummer = of("0")
    }
}
