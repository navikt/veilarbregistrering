package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerException
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerFeil
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

@ExtendWith(MockServerExtension::class)
internal class OppfolgingClientTest(private val mockServer: ClientAndServer) {
    private lateinit var oppfolgingClient: OppfolgingClient


    @BeforeEach
    fun setup() {
        mockServer.reset()
        oppfolgingClient = buildClient(jacksonObjectMapper().findAndRegisterModules())
    }

    private fun buildClient(objectMapper: ObjectMapper): OppfolgingClient {
        mockkStatic(RequestContext::class)
        val systemUserTokenProvider: SystemUserTokenProvider = mockk()
        val httpServletRequest: HttpServletRequest = mockk()
        every { RequestContext.servletRequest() } returns httpServletRequest
        every { httpServletRequest.getHeader(HttpHeaders.COOKIE) } returns "czas Å\u009Brodkowoeuropejski standardowy"
        every { systemUserTokenProvider.systemUserToken } returns "testToken"
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return OppfolgingClient(objectMapper, mockk(relaxed = true), baseUrl, systemUserTokenProvider) { "TOKEN" }.also { oppfolgingClient = it }
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
    fun `skal returnere respons for hentOppfolgingstatus`() {
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/oppfolging")).respond(
            HttpResponse.response()
                    .withStatusCode(200)
                    .withBody(ikkeUnderOppfolgingBody(), MediaType.JSON_UTF_8)
        )
        Assertions.assertNotNull(
            oppfolgingClient.hentOppfolgingsstatus(Foedselsnummer.of(FNR.fnr))
        )
    }

    @Test
    fun testAtReaktiveringFeilerDersomArenaSierAtBrukerErUnderOppfolging() {
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/oppfolging"))
            .respond(
                HttpResponse.response().withBody(settOppfolgingOgReaktivering(true, false), MediaType.JSON_UTF_8)
                    .withStatusCode(200)
            )
        assertThrows<RuntimeException> {
            oppfolgingClient.reaktiverBruker(Fnr("10108000398")) }
    }

    @Test
    fun testAtReaktiveringGirOKDersomArenaSierAtBrukerKanReaktiveres() {
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/oppfolging"))
            .respond(
                HttpResponse.response().withBody(settOppfolgingOgReaktivering(false, true), MediaType.JSON_UTF_8)
                    .withStatusCode(200)
            )
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/reaktiverbruker")).respond(
            HttpResponse.response().withStatusCode(204)
        )
        oppfolgingClient.reaktiverBruker(Fnr("10108000398"))
    }

    @Test
    fun `skal kaste riktig feil ved manglende oppholdstillatelse`() {
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
    fun `skal kaste riktig feil dersom bruker ikke kan reaktiveres`() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(
            HttpResponse.response().withBody(FileToJson.toJson("/oppfolging/kanIkkeReaktiveres.json"))
                .withStatusCode(403)
        )
        val exception: AktiverBrukerException = assertThrows {
            oppfolgingClient.aktiverBruker(AktiverBrukerData(FNR, Innsatsgruppe.SITUASJONSBESTEMT_INNSATS))
        }
        assertThat(exception.aktiverBrukerFeil).isEqualTo(AktiverBrukerFeil.BRUKER_KAN_IKKE_REAKTIVERES)
    }

    private fun settOppfolgingOgReaktivering(oppfolging: Boolean, reaktivering: Boolean): String {
        return "{\"kanReaktiveres\": $reaktivering, \"underOppfolging\": $oppfolging}"
    }

    private fun ikkeUnderOppfolgingBody(): String = """
        { "underOppfolging": false }
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