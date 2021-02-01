package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import com.google.common.net.MediaType
import no.nav.common.log.MDCConstants
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeTestdataBuilder
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperioderTestdataBuilder.arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.log.CallId.leggTilCallId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse.response
import org.slf4j.MDC
import java.time.LocalDate


class FormidlingsgruppeRestClientTest {

    private lateinit var mockServer: ClientAndServer

    @AfterEach
    fun tearDown() {
        mockServer.stop()
    }

    @BeforeEach
    fun setup() {
        leggTilCallId()
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT)
    }

    private fun buildClient(): FormidlingsgruppeRestClient {
        val baseUrl = "http://$MOCKSERVER_URL:$MOCKSERVER_PORT"
        return FormidlingsgruppeRestClient(baseUrl) { "arenaOrdsTokenProvider" }
    }

    @Test
    fun `skal hente formidlingsgruppe for gitt person`() {
        val formidlingsgruppeGateway: FormidlingsgruppeGateway = FormidlingsgruppeGatewayImpl(buildClient())

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
                Foedselsnummer.of("12345612345"),
                Periode.of(
                        LocalDate.of(2020, 1, 10),
                        LocalDate.of(2020, 1, 11)))

        assertThat(arbeidssokerperioder).isEqualTo(
                arbeidssokerperioder()
                        .arbeidssokerperiode(ArbeidssokerperiodeTestdataBuilder
                                .medIserv()
                                .fra(LocalDate.of(2020, 1, 12))
                                .til(LocalDate.of(2020, 1, 11)))
                        .arbeidssokerperiode(ArbeidssokerperiodeTestdataBuilder
                                .medArbs()
                                .fra(LocalDate.of(2020, 1, 12))
                                .til(LocalDate.of(2020, 2, 20)))
                        .arbeidssokerperiode(ArbeidssokerperiodeTestdataBuilder
                                .medIserv()
                                .fra(LocalDate.of(2020, 2, 21))
                                .til(LocalDate.of(2020, 3, 11)))
                        .arbeidssokerperiode(ArbeidssokerperiodeTestdataBuilder
                                .medArbs()
                                .fra(LocalDate.of(2020, 3, 12))
                                .til(null))
                        .build())
    }

    @Test
    fun `skal gi empty for ukjent person`() {
        val formidlingsgruppeGateway: FormidlingsgruppeGateway = FormidlingsgruppeGatewayImpl(buildClient())

        mockServer.`when`(
            HttpRequest
                .request()
                .withMethod("GET")
                .withPath("/v1/person/arbeidssoeker/formidlingshistorikk")
                .withQueryStringParameter("fnr", "11118035157"))
            .respond(response()
                .withStatusCode(404))

        val arbeidssokerperioder = formidlingsgruppeGateway.finnArbeissokerperioder(
            Foedselsnummer.of("11118035157"),
            Periode.of(
                LocalDate.of(2020, 1, 10),
                LocalDate.of(2020, 1, 11)))

        assertThat(arbeidssokerperioder.asList()).isEmpty()
    }

    companion object {
        private const val MOCKSERVER_URL = "localhost"
        private const val MOCKSERVER_PORT = 1083
    }

}