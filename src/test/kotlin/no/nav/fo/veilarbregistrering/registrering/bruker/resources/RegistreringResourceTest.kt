package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import io.mockk.*
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering
import no.nav.fo.veilarbregistrering.registrering.bruker.HentRegistreringService
import no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringStatusService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [RegistreringResourceConfig::class])
class RegistreringResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val registreringResource: RegistreringResource,
    @Autowired private val tilgangskontrollService: TilgangskontrollService,
    @Autowired private val authContextHolder: AuthContextHolder,
    @Autowired private val pdlOppslagGateway: PdlOppslagGateway,
    @Autowired private val hentRegistreringService: HentRegistreringService,
    @Autowired private val startRegistreringStatusService: StartRegistreringStatusService,
) {
    private lateinit var request: HttpServletRequest
    private val CONSUMER_ID_TEST = "TEST"

    @BeforeEach
    fun setup() {
        clearAllMocks()
        mockkStatic(RequestContext::class)
        request = mockk(relaxed = true)
        every { RequestContext.servletRequest() } returns request
        every { tilgangskontrollService.erVeileder() } returns true
        every { authContextHolder.subject} returns Optional.of("sub")
        every { authContextHolder.idTokenClaims } returns Optional.empty()
    }

    @Test
    fun `serialiserer startregistrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { authContextHolder.erEksternBruker() } returns false
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { startRegistreringStatusService.hentStartRegistreringStatus(any(), any()) } returns START_REGISTRERING_STATUS
        val expected = FileToJson.toJson("/registrering/startregistrering.json")

        val result = mvc.get("/api/startregistrering") { header("Nav-Consumer-Id", CONSUMER_ID_TEST) }
            .andExpect { status { isOk() } }
            .andReturn().response.contentAsString

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `serialiserer tom registrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { authContextHolder.erEksternBruker() } returns false
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { hentRegistreringService.hentBrukerregistrering(any()) } returns null

        val result = mvc.get("/api/registrering")
            .andExpect {
                status { isNoContent() }
            }
            .andReturn().response.contentAsString

        assertThat(result).isEqualTo("")
    }

    @Test
    fun `serialiserer registrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { authContextHolder.erEksternBruker() } returns false
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { hentRegistreringService.hentBrukerregistrering(any()) } returns BrukerRegistreringWrapperFactory.create(
            GYLDIG_BRUKERREGISTRERING,
            null
        )

        val result = mvc.get("/api/registrering")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
            }
            .andReturn().response.getContentAsString(StandardCharsets.UTF_8)

        assertThat(result).isEqualTo(REGISTRERING_RESPONSE)
    }

    @Test
    fun `serialiserer registrering med profilering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { authContextHolder.erEksternBruker() } returns false
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { hentRegistreringService.hentBrukerregistrering(any()) } returns BrukerRegistreringWrapperFactory.create(
            GYLDIG_BRUKERREGISTRERING_M_PROF,
            null
        )

        val result = mvc.get("/api/registrering")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
            }
            .andReturn().response.getContentAsString(StandardCharsets.UTF_8)

        assertThat(result).isEqualTo(REGISTRERING_RESPONSE_M_PROF)
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedHentingAvStartRegistreringsstatus() {
        every { startRegistreringStatusService.hentStartRegistreringStatus(any(), any()) } returns StartRegistreringStatusDto()
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { authContextHolder.erEksternBruker() } returns false
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        registreringResource.hentStartRegistreringStatus(CONSUMER_ID_TEST)
        verify(exactly = 1) { tilgangskontrollService.sjekkLesetilgangTilBruker(any(), any()) }
    }

    @Test
    fun skalFeileVedHentingAvStartRegistreringsstatusMedUgyldigFnr() {
        every { startRegistreringStatusService.hentStartRegistreringStatus(any(), any()) } returns StartRegistreringStatusDto()

        assertThrows<RuntimeException>("Fødselsnummer ikke gyldig.") { registreringResource.hentRegistrering() }
        verify { tilgangskontrollService wasNot Called }
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedHentingAvRegistrering() {
        every { hentRegistreringService.hentBrukerregistrering(any()) } returns BrukerRegistreringWrapperFactory.create(
            gyldigBrukerRegistrering(), null
        )
        every { hentRegistreringService.hentSykmeldtRegistrering(any()) } returns null
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { authContextHolder.erEksternBruker() } returns false
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        registreringResource.hentRegistrering()
        verify(exactly = 1) { tilgangskontrollService.sjekkLesetilgangTilBruker(any(), any()) }
    }

    companion object {
        private val IDENT = Foedselsnummer("10108000398") //Aremark fiktivt fnr.";
        private val IDENTER = Identer(
            mutableListOf(
                Ident(IDENT.stringValue(), false, Gruppe.FOLKEREGISTERIDENT),
                Ident("22222222222", false, Gruppe.AKTORID)
            )
        )
        private val START_REGISTRERING_STATUS = StartRegistreringStatusDto()
        private val time = LocalDateTime.of(2020, 1, 11, 15, 50, 20)
        private val profilering = lagProfilering()
        private val GYLDIG_BRUKERREGISTRERING = gyldigBrukerRegistrering(opprettetDato = time)
        private val GYLDIG_BRUKERREGISTRERING_M_PROF =
            gyldigBrukerRegistrering(opprettetDato = time, profilering = profilering)

        private val REGISTRERING_RESPONSE =
            "{\"registrering\":{\"id\":0,\"opprettetDato\":\"$time\",\"besvarelse\":{\"utdanning\":\"HOYERE_UTDANNING_5_ELLER_MER\",\"utdanningBestatt\":\"JA\",\"utdanningGodkjent\":\"JA\",\"helseHinder\":\"NEI\",\"andreForhold\":\"NEI\",\"sisteStilling\":\"HAR_HATT_JOBB\",\"dinSituasjon\":\"JOBB_OVER_2_AAR\",\"fremtidigSituasjon\":null,\"tilbakeIArbeid\":null},\"teksterForBesvarelse\":[{\"sporsmalId\":\"utdanning\",\"sporsmal\":\"Hva er din høyeste fullførte utdanning?\",\"svar\":\"Høyere utdanning (5 år eller mer)\"},{\"sporsmalId\":\"utdanningBestatt\",\"sporsmal\":\"Er utdanningen din bestått?\",\"svar\":\"Ja\"},{\"sporsmalId\":\"utdanningGodkjent\",\"sporsmal\":\"Er utdanningen din godkjent i Norge?\",\"svar\":\"Nei\"},{\"sporsmalId\":\"helseHinder\",\"sporsmal\":\"Trenger du oppfølging i forbindelse med helseutfordringer?\",\"svar\":\"Nei\"},{\"sporsmalId\":\"andreForhold\",\"sporsmal\":\"Trenger du oppfølging i forbindelse med andre utfordringer?\",\"svar\":\"Nei\"},{\"sporsmalId\":\"sisteStilling\",\"sporsmal\":\"Din siste jobb\",\"svar\":\"Har hatt jobb\"},{\"sporsmalId\":\"dinSituasjon\",\"sporsmal\":\"Hvorfor registrerer du deg?\",\"svar\":\"Jeg er permittert eller vil bli permittert\"}],\"sisteStilling\":{\"label\":\"yrkesbeskrivelse\",\"konseptId\":1246345,\"styrk08\":\"12345\"},\"profilering\":null,\"manueltRegistrertAv\":null},\"type\":\"ORDINAER\"}"
        private const val REGISTRERING_RESPONSE_M_PROF =
            "{\"registrering\":{\"id\":0,\"opprettetDato\":\"2020-01-11T15:50:20\",\"besvarelse\":{\"utdanning\":\"HOYERE_UTDANNING_5_ELLER_MER\",\"utdanningBestatt\":\"JA\",\"utdanningGodkjent\":\"JA\",\"helseHinder\":\"NEI\",\"andreForhold\":\"NEI\",\"sisteStilling\":\"HAR_HATT_JOBB\",\"dinSituasjon\":\"JOBB_OVER_2_AAR\",\"fremtidigSituasjon\":null,\"tilbakeIArbeid\":null},\"teksterForBesvarelse\":[{\"sporsmalId\":\"utdanning\",\"sporsmal\":\"Hva er din høyeste fullførte utdanning?\",\"svar\":\"Høyere utdanning (5 år eller mer)\"},{\"sporsmalId\":\"utdanningBestatt\",\"sporsmal\":\"Er utdanningen din bestått?\",\"svar\":\"Ja\"},{\"sporsmalId\":\"utdanningGodkjent\",\"sporsmal\":\"Er utdanningen din godkjent i Norge?\",\"svar\":\"Nei\"},{\"sporsmalId\":\"helseHinder\",\"sporsmal\":\"Trenger du oppfølging i forbindelse med helseutfordringer?\",\"svar\":\"Nei\"},{\"sporsmalId\":\"andreForhold\",\"sporsmal\":\"Trenger du oppfølging i forbindelse med andre utfordringer?\",\"svar\":\"Nei\"},{\"sporsmalId\":\"sisteStilling\",\"sporsmal\":\"Din siste jobb\",\"svar\":\"Har hatt jobb\"},{\"sporsmalId\":\"dinSituasjon\",\"sporsmal\":\"Hvorfor registrerer du deg?\",\"svar\":\"Jeg er permittert eller vil bli permittert\"}],\"sisteStilling\":{\"label\":\"yrkesbeskrivelse\",\"konseptId\":1246345,\"styrk08\":\"12345\"},\"profilering\":{\"innsatsgruppe\":\"STANDARD_INNSATS\",\"alder\":62,\"jobbetSammenhengendeSeksAvTolvSisteManeder\":false},\"manueltRegistrertAv\":null},\"type\":\"ORDINAER\"}"
    }
}

@Configuration
private class RegistreringResourceConfig {
    @Bean
    fun registreringResource(
        tilgangskontrollService: TilgangskontrollService,
        userService: UserService,
        brukerRegistreringService: BrukerRegistreringService,
        hentRegistreringService: HentRegistreringService,
        startRegistreringStatusService: StartRegistreringStatusService
    ) = RegistreringResource(
        tilgangskontrollService,
        userService,
        hentRegistreringService,
        startRegistreringStatusService
    )

    @Bean
    fun tilgangskontrollService(): TilgangskontrollService = mockk(relaxed = true)

    @Bean
    fun pdlOppslagGateway(): PdlOppslagGateway = mockk()

    @Bean
    fun brukerRegistreringService(): BrukerRegistreringService = mockk(relaxed = true)

    @Bean
    fun hentRegistreringService(): HentRegistreringService = mockk()

    @Bean
    fun startRegistreringStatusService(): StartRegistreringStatusService = mockk()

    @Bean
    fun authContextHolder(): AuthContextHolder = mockk()

    @Bean
    fun userService(pdlOppslagGateway: PdlOppslagGateway, authContextHolder: AuthContextHolder): UserService =
        UserService(pdlOppslagGateway, authContextHolder)
}