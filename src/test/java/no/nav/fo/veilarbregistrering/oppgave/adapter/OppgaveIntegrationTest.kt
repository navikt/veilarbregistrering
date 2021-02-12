package no.nav.fo.veilarbregistrering.oppgave.adapter

import io.mockk.every
import io.mockk.mockk
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.oppgave.*
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import java.time.LocalDate
import java.util.*
import javax.inject.Provider
import javax.servlet.http.HttpServletRequest

class OppgaveIntegrationTest {
    private lateinit var mockServer: ClientAndServer
    private lateinit var oppgaveService: OppgaveService
    private lateinit var oppgaveRouter: OppgaveRouter

    @AfterEach
    fun tearDown() {
        mockServer.stop()
    }

    @BeforeEach
    fun setup() {
        val oppgaveRepository: OppgaveRepository = mockk(relaxed = true)
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT)
        val oppgaveGateway: OppgaveGateway = OppgaveGatewayImpl(buildClient())
        oppgaveRouter = mockk()
        oppgaveService = CustomOppgaveService(
            oppgaveGateway,
            oppgaveRepository,
            oppgaveRouter,
            { _: AktorId?, _: OppgaveType? -> }
        )
    }

    private fun buildClient(): OppgaveRestClient {
        val systemUserTokenProvider: SystemUserTokenProvider = mockk()
        val httpServletRequestProvider: Provider<HttpServletRequest> = mockk()
        val httpServletRequest: HttpServletRequest = mockk()
        every { httpServletRequestProvider.get() } returns httpServletRequest
        every { httpServletRequest.getHeader(any()) } returns ""
        every { systemUserTokenProvider.systemUserToken } returns "testToken"
        val baseUrl = "http://$MOCKSERVER_URL:$MOCKSERVER_PORT"
        return OppgaveRestClient(baseUrl, systemUserTokenProvider)
    }

    @Test
    fun `vellykket opprettelse av oppgave skal gi 201`() {
        val dagensdato = LocalDate.of(2020, 5, 27).toString()
        val toArbeidsdagerSenere = LocalDate.of(2020, 5, 29).toString()
        every { oppgaveRouter.hentEnhetsnummerFor(BRUKER) } returns Optional.of(Enhetnr.of("0301"))
        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/oppgaver")
                        .withBody("{" +
                                "\"aktoerId\":\"12e1e3\"," +
                                "\"beskrivelse\":\"" +
                                "Brukeren får ikke registrert seg som arbeidssøker fordi bruker står som utvandret i TPS og ikke er befolket i Arena, " +
                                "og har selv opprettet denne oppgaven. " +
                                "\\n\\nRing bruker og følg vanlig rutine for slike tilfeller." +
                                "\\n\\nHar oppgaven havnet i feil oppgaveliste? Da ønsker vi som har utviklet løsningen tilbakemelding på dette. " +
                                "Meld sak her: https://jira.adeo.no/plugins/servlet/desk/portal/541/create/3384. Takk!\"," +
                                "\"tema\":\"OPP\"," +
                                "\"oppgavetype\":\"KONT_BRUK\"," +
                                "\"fristFerdigstillelse\":\"" +
                                toArbeidsdagerSenere +
                                "\"," +
                                "\"aktivDato\":\"" +
                                dagensdato +
                                "\"," +
                                "\"prioritet\":\"NORM\"," +
                                "\"tildeltEnhetsnr\":\"0301\"" +
                                "}"))
                .respond(HttpResponse.response()
                        .withStatusCode(201)
                        .withBody(okRegistreringBody(), MediaType.JSON_UTF_8))
        val oppgaveResponse =
            oppgaveService.opprettOppgave(
                BRUKER,
                    OppgaveType.UTVANDRET
            ) //TODO provide subject somehow

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

    private class CustomOppgaveService(
            oppgaveGateway: OppgaveGateway?,
            oppgaveRepository: OppgaveRepository?,
            oppgaveRouter: OppgaveRouter?,
            kontaktBrukerHenvendelseProducer: KontaktBrukerHenvendelseProducer?) : OppgaveService(oppgaveGateway, oppgaveRepository, oppgaveRouter, kontaktBrukerHenvendelseProducer, mockk(relaxed = true)) {
        override fun idag(): LocalDate {
            return LocalDate.of(2020, 5, 27)
        }
    }

    companion object {
        private const val MOCKSERVER_URL = "localhost"
        private const val MOCKSERVER_PORT = 1081
        val BRUKER: Bruker = Bruker.of(Foedselsnummer.of("12345678911"), AktorId.of("12e1e3"))
    }
}