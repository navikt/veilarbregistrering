package no.nav.fo.veilarbregistrering.oppgave.adapter

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.oppgave.Oppgave
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import java.time.LocalDate
import javax.inject.Provider
import javax.servlet.http.HttpServletRequest

@ExtendWith(MockServerExtension::class)
internal class OppgaveGatewayTest(private val mockServer: ClientAndServer) {

    private fun buildClient(): OppgaveRestClient {
        val httpServletRequestProvider: Provider<HttpServletRequest> = mockk()
        val httpServletRequest: HttpServletRequest = mockk()
        every { httpServletRequestProvider.get() } returns httpServletRequest
        every { httpServletRequest.getHeader(any()) } returns ""
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return OppgaveRestClient(baseUrl, mockk(relaxed = true)) { "testToken" }
    }

    @Test
    fun `vellykket opprettelse av oppgave skal gi 201`() {
        val oppgaveGateway: OppgaveGateway = OppgaveGatewayImpl(buildClient())
        val dagensdato = LocalDate.of(2020, 5, 26)
        val toDagerSenere = LocalDate.of(2020, 5, 28)
        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/oppgaver")
                        .withBody("{" +
                                "\"aktoerId\":\"12e1e3\"," +
                                "\"beskrivelse\":\"" +
                                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                                "og har selv opprettet denne oppgaven." +
                                "\\n\\nFølg rutinen som er beskrevet for registreringen av arbeids- og oppholdstillatelse: " +
                                "https://navno.sharepoint.com/sites/fag-og-ytelser-regelverk-og-rutiner/SitePages/Registrering-av-arbeids--og-oppholdstillatelse.aspx\"," +
                                "\"tema\":\"OPP\"," +
                                "\"oppgavetype\":\"KONT_BRUK\"," +
                                "\"fristFerdigstillelse\":\"" +
                                toDagerSenere.toString() +
                                "\"," +
                                "\"aktivDato\":\"" +
                                dagensdato.toString() +
                                "\"," +
                                "\"prioritet\":\"NORM\"," +
                                "\"tildeltEnhetsnr\":null" +
                                "}"))
                .respond(HttpResponse.response()
                        .withStatusCode(201)
                        .withBody(okRegistreringBody(), MediaType.JSON_UTF_8))
        val oppgave = Oppgave.opprettOppgave(
                AktorId("12e1e3"),
                null, OppgaveType.OPPHOLDSTILLATELSE,
                dagensdato)
        val oppgaveResponse = oppgaveGateway.opprett(oppgave) //TODO provide subject somehow
        Assertions.assertThat(oppgaveResponse.id).isEqualTo(5436732)
        Assertions.assertThat(oppgaveResponse.tildeltEnhetsnr).isEqualTo("3012")
    }

    private fun okRegistreringBody(): String {
        return """
            {
            "id": "5436732",
            "aktoerId": "12e1e3",
            "tildeltEnhetsnr": "3012"
            }
            """.trimIndent()
    }
}