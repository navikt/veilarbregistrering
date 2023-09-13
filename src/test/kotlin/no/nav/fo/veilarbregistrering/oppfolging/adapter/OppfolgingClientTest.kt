package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.HttpHeaders
import kotlin.test.assertFalse

@ExtendWith(MockServerExtension::class)
internal class OppfolgingClientTest(private val mockServer: ClientAndServer) {
    private lateinit var oppfolgingClient: OppfolgingClient
    private lateinit var httpServletRequest: HttpServletRequest


    @BeforeEach
    fun setup() {
        System.setProperty("VEILARBOPPFOLGINGAPI_CLUSTER", "dev-gcp")
        mockServer.reset()
        httpServletRequest = mockk()
        oppfolgingClient = buildClient(jacksonObjectMapper().findAndRegisterModules())
    }

    private fun buildClient(objectMapper: ObjectMapper): OppfolgingClient {
        mockkStatic(RequestContext::class)
        every { RequestContext.servletRequest() } returns httpServletRequest
        every { httpServletRequest.getHeader(HttpHeaders.COOKIE) } returns "czas Å\u009Brodkowoeuropejski standardowy"
        every { httpServletRequest.cookies } returns emptyArray()
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return OppfolgingClient(objectMapper, mockk(relaxed = true), baseUrl, mockk(relaxed = true)) { "TOKEN" }.also { oppfolgingClient = it }
    }

    @Test
    fun skal_kaste_RuntimeException_ved_vilkaarlig_feil_som_ikke_er_haandtert() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(
            HttpResponse.response().withStatusCode(404)
        )
        assertThrows<RuntimeException> {
            oppfolgingClient.aktiverBruker(
                AktiverBrukerData(
                    FNR,
                    Innsatsgruppe.SITUASJONSBESTEMT_INNSATS
                )
            )
        }
    }

    @Test
    fun skal_returnere_response_ved_204() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(
            HttpResponse.response()
                    .withStatusCode(204)
                    .withBody(okRegistreringBody(), MediaType.JSON_UTF_8)
        )
        Assertions.assertNotNull(
            oppfolgingClient.aktiverBruker(
                AktiverBrukerData(
                    FNR,
                    Innsatsgruppe.SITUASJONSBESTEMT_INNSATS
                )
            )
        )
    }

    @Test
    fun testAtReaktiveringFeilerDersomArenaSierAtBrukerErUnderOppfolging() {
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/oppfolging"))
            .respond(
                HttpResponse.response().withBody(settOppfolgingOgReaktivering(oppfolging = true, reaktivering = false), MediaType.JSON_UTF_8)
                    .withStatusCode(200)
            )
        assertThrows<RuntimeException> {
            oppfolgingClient.reaktiverBruker(Fnr("10108000398")) }
    }

    @Test
    fun testAtReaktiveringGirOKDersomArenaSierAtBrukerKanReaktiveres() {
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/oppfolging"))
            .respond(
                HttpResponse.response().withBody(settOppfolgingOgReaktivering(oppfolging = false, reaktivering = true), MediaType.JSON_UTF_8)
                    .withStatusCode(200)
            )
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/reaktiverbruker")).respond(
            HttpResponse.response().withStatusCode(204)
        )
        oppfolgingClient.reaktiverBruker(Fnr("10108000398"))
    }

    @Test
    fun `skal kaste AktiverBrukerException ved manglende oppholdstillatelse for aktivering`() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(
            HttpResponse.response().withBody(FileToJson.toJson("/oppfolging/manglerOppholdstillatelse.json"))
                .withStatusCode(403)
        )
        val exception: AktiverBrukerException = assertThrows {
            oppfolgingClient.aktiverBruker(AktiverBrukerData(FNR, Innsatsgruppe.SITUASJONSBESTEMT_INNSATS))
        }
        assertThat(exception.aktiverBrukerFeil).isEqualTo(AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE)
    }

    @Test
    fun `skal kaste AktiverBrukerException ved manglende oppholdstillatelse for reaktivering`() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/reaktiverbruker")).respond(
            HttpResponse.response().withBody(FileToJson.toJson("/oppfolging/manglerOppholdstillatelse.json"))
                .withStatusCode(403)
        )
        val exception: AktiverBrukerException = assertThrows {
            oppfolgingClient.reaktiverBruker(FNR)
        }
        assertThat(exception.aktiverBrukerFeil).isEqualTo(AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE)
    }

    @Test
    fun `skal kaste AktiverBrukerException ved manglende oppholdstillatelse for sykmeldt-aktivering`() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverSykmeldt")).respond(
            HttpResponse.response().withBody(FileToJson.toJson("/oppfolging/manglerOppholdstillatelse.json"))
                .withStatusCode(403)
        )
        val exception: AktiverBrukerException = assertThrows {
            oppfolgingClient.aktiverSykmeldt(SykmeldtBrukerType.SKAL_TIL_NY_ARBEIDSGIVER, Foedselsnummer(FNR.fnr))
        }
        assertThat(exception.aktiverBrukerFeil).isEqualTo(AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE)
    }

    @Test
    fun `skal kaste AktiverBrukerException dersom bruker ikke kan reaktiveres`() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(
            HttpResponse.response().withBody(FileToJson.toJson("/oppfolging/kanIkkeReaktiveres.json"))
                .withStatusCode(403)
        )
        val exception: AktiverBrukerException = assertThrows {
            oppfolgingClient.aktiverBruker(AktiverBrukerData(FNR, Innsatsgruppe.SITUASJONSBESTEMT_INNSATS))
        }
        assertThat(exception.aktiverBrukerFeil).isEqualTo(AktiverBrukerFeil.BRUKER_KAN_IKKE_REAKTIVERES)
    }

    @Test
    fun `skal hente og deserialisere er bruker under oppfølging`() {
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/v2/oppfolging"))
            .respond(
                HttpResponse.response().withBody(ikkeUnderOppfolgingBody(), MediaType.JSON_UTF_8)
                    .withStatusCode(200)
            )

        val response = oppfolgingClient.erBrukerUnderOppfolging(Foedselsnummer(FNR.fnr))
        assertFalse(response.erUnderOppfolging)
    }

    private fun settOppfolgingOgReaktivering(oppfolging: Boolean, reaktivering: Boolean): String {
        return "{\"kanReaktiveres\": $reaktivering, \"underOppfolging\": $oppfolging}"
    }

    private fun ikkeUnderOppfolgingBody(): String = """
        { "erUnderOppfolging": false }
    """.trimIndent()

    private fun okRegistreringBody(): String {
        return """
            {
            "Status": "STATUS_SUKSESS"
            }
            """.trimIndent()
    }

    companion object {
        private val FNR = Fnr("10108000398") //Aremark fiktivt fnr.";
    }
}