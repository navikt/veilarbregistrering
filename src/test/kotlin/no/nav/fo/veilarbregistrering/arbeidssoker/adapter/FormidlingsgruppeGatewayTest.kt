package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeTestdataBuilder
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperioderTestdataBuilder.Companion.arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.log.CallId.leggTilCallId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType
import java.time.LocalDate

@ExtendWith(MockServerExtension::class)
class FormidlingsgruppeGatewayTest(private val mockServer: ClientAndServer) {

    private lateinit var formidlingsgruppeGateway: FormidlingsgruppeGateway

    @BeforeEach
    fun setup() {
        leggTilCallId()
        mockServer.reset()
        formidlingsgruppeGateway = FormidlingsgruppeGatewayImpl(buildClient())
    }

    private fun buildClient(): FormidlingsgruppeRestClient {
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return FormidlingsgruppeRestClient(baseUrl, mockk(relaxed = true)) { "arenaOrdsTokenProvider" }
    }

    @Test
    fun `skal hente formidlingsgruppe for gitt person og filtrere bort formidlingshistorikk med IARBS og ISERV`() {
        val json = FileToJson.toJson("/arbeidssoker/formidlingshistorikk.json")

        mockServer.`when`(
                HttpRequest
                        .request()
                        .withMethod("GET")
                        .withPath("/v1/person/arbeidssoeker/formidlingshistorikk"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(json, MediaType.JSON_UTF_8))

        val arbeidssokerperioder = formidlingsgruppeGateway.finnArbeissokerperioder(
                Foedselsnummer("12345612345"),
                Periode(
                        LocalDate.of(2020, 1, 10),
                        LocalDate.of(2020, 1, 11)))

        assertThat(arbeidssokerperioder).isEqualTo(
                arbeidssokerperioder()
                        .arbeidssokerperiode(ArbeidssokerperiodeTestdataBuilder
                                .arbeidssokerperiode()
                                .fra(LocalDate.of(2020, 1, 12))
                                .til(LocalDate.of(2020, 2, 20)))
                        .arbeidssokerperiode(ArbeidssokerperiodeTestdataBuilder
                                .arbeidssokerperiode()
                                .fra(LocalDate.of(2020, 3, 12))
                                .til(null))
                        .build())
    }

    @Test
    fun `skal parse formidlingsgruppe for person uten historikk`() {
        val json = FileToJson.toJson("/arbeidssoker/formidlingshistorikk_missing.json")

        mockServer.`when`(
            HttpRequest
                .request()
                .withMethod("GET")
                .withPath("/v1/person/arbeidssoeker/formidlingshistorikk")
        )
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody(json, MediaType.JSON_UTF_8)
            )

        formidlingsgruppeGateway.finnArbeissokerperioder(
            Foedselsnummer("12345612345"),
            Periode(
                LocalDate.of(2020, 1, 10),
                LocalDate.of(2020, 1, 11)))
    }

    @Test
    fun `skal parse formidlingsgruppe for person med null historikk`() {
        val json = FileToJson.toJson("/arbeidssoker/formidlingshistorikk_null.json")

        mockServer.`when`(
            HttpRequest
                .request()
                .withMethod("GET")
                .withPath("/v1/person/arbeidssoeker/formidlingshistorikk")
        )
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody(json, MediaType.JSON_UTF_8)
            )

        formidlingsgruppeGateway.finnArbeissokerperioder(
            Foedselsnummer("12345612345"),
            Periode(
                LocalDate.of(2020, 1, 10),
                LocalDate.of(2020, 1, 11)))
    }

    @Test
    fun `skal gi empty for ukjent person`() {
        mockServer.`when`(
            HttpRequest
                .request()
                .withMethod("GET")
                .withPath("/v1/person/arbeidssoeker/formidlingshistorikk")
                .withQueryStringParameter("fnr", "11118035157"))
            .respond(response()
                .withStatusCode(404))

        val arbeidssokerperioder = formidlingsgruppeGateway.finnArbeissokerperioder(
            Foedselsnummer("11118035157"),
            Periode(
                LocalDate.of(2020, 1, 10),
                LocalDate.of(2020, 1, 11)))

        assertThat(arbeidssokerperioder.asList()).isEmpty()
    }
}
