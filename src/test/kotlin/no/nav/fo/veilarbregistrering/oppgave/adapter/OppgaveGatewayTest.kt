package no.nav.fo.veilarbregistrering.oppgave.adapter

import com.google.common.net.MediaType
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.brukerdialog.security.domain.IdentType
import no.nav.common.auth.SsoToken
import no.nav.common.auth.Subject
import no.nav.common.auth.SubjectHandler
import no.nav.common.oidc.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.oppgave.Oppgave
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import java.time.LocalDate
import java.util.*
import javax.inject.Provider
import javax.servlet.http.HttpServletRequest

internal class OppgaveGatewayTest {

    private lateinit var mockServer: ClientAndServer

    @AfterEach
    fun tearDown() {
        mockServer.stop()
    }

    @BeforeEach
    fun setup() {
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT)
    }

    private fun buildClient(): OppgaveRestClient {
        val systemUserTokenProvider: SystemUserTokenProvider = mock()
        val httpServletRequestProvider: Provider<HttpServletRequest> = mock()
        val httpServletRequest: HttpServletRequest = mock()
        whenever(httpServletRequestProvider.get()).thenReturn(httpServletRequest)
        whenever(httpServletRequest.getHeader(ArgumentMatchers.any())).thenReturn("")
        whenever(systemUserTokenProvider.systemUserAccessToken).thenReturn("testToken")
        val baseUrl = "http://" + MOCKSERVER_URL + ":" + MOCKSERVER_PORT
        return OppgaveRestClient(baseUrl, systemUserTokenProvider)
    }

    @Test
    fun `vellykket opprettelse av oppgave skal gi 201`() {
        val oppgaveGateway: OppgaveGateway = OppgaveGatewayImpl(buildClient())
        val dagensdato = LocalDate.of(2020, 5, 26)
        val toDagerSenere = LocalDate.of(2020, 5, 28)
        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/oppgaver")
                        .withBody("{" +
                                "\"aktoerId\":\"12e1e3\"," +
                                "\"beskrivelse\":\"" +
                                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                                "og har selv opprettet denne oppgaven." +
                                "\\n\\nFølg rutinen som er beskrevet for registreringen av arbeids- og oppholdstillatelse: " +
                                "https://navno.sharepoint.com/sites/fag-og-ytelser-regelverk-og-rutiner/SitePages/Registrering-av-arbeids--og-oppholdstillatelse.aspx" +
                                "\\n\\nHar oppgaven havnet i feil oppgaveliste? Da ønsker vi som har utviklet løsningen tilbakemelding på dette. Meld sak her: https://jira.adeo.no/plugins/servlet/desk/portal/541/create/3384. Takk!\"," +
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
                AktorId.of("12e1e3"),
                null, OppgaveType.OPPHOLDSTILLATELSE,
                dagensdato)
        val oppgaveResponse = SubjectHandler.withSubject<OppgaveResponse>(
                Subject("foo", IdentType.EksternBruker, SsoToken.oidcToken("bar", HashMap<String, Any?>()))
        ) { oppgaveGateway.opprett(oppgave) }
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

    companion object {
        private const val MOCKSERVER_URL = "localhost"
        private const val MOCKSERVER_PORT = 1083
    }
}