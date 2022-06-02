package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import io.mockk.*
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar
import no.nav.fo.veilarbregistrering.besvarelse.HelseHinderSvar
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
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
    @Autowired private val autorisasjonService: AutorisasjonService,
    @Autowired private val authContextHolder: AuthContextHolder,
    @Autowired private val pdlOppslagGateway: PdlOppslagGateway,
    @Autowired private val brukerRegistreringService: BrukerRegistreringService,
    @Autowired private val hentRegistreringService: HentRegistreringService,
    @Autowired private val startRegistreringStatusService: StartRegistreringStatusService,
) {
    private lateinit var request: HttpServletRequest

    @BeforeEach
    fun setup() {
        clearAllMocks()
        mockkStatic(RequestContext::class)
        request = mockk(relaxed = true)
        every { RequestContext.servletRequest() } returns request
        every { autorisasjonService.erVeileder() } returns true
        every { authContextHolder.subject} returns Optional.of("sub")
        every { authContextHolder.idTokenClaims } returns Optional.empty()
    }

    @Test
    fun `startregistrersykmeldt har riktig status og responsbody`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        val responseString = mvc.post("/api/startregistrersykmeldt") {
            contentType = MediaType.APPLICATION_JSON
            content = FileToJson.toJson("/registrering/startregistrersykmeldt.json")
        }.andExpect {
            status { isNoContent() }
        }.andReturn().response.contentAsString

        assertThat(responseString).isNullOrEmpty()
    }

    @Test
    fun `startreaktivering returnerer riktig status og responsbody`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        val responseString = mvc.post("/api/startreaktivering").andExpect {
            status { isNoContent() }
        }.andReturn().response.contentAsString

        assertThat(responseString).isNullOrEmpty()
    }

    @Test
    fun `Kan parse registrering json i requestbody til objekt ok`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        val responseString = mvc.post("/api/startregistrering") {
            contentType = MediaType.APPLICATION_JSON
            content = REGISTRERING_REQUEST
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        println(responseString)

    }

    @Test
    fun `serialiserer startregistrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { startRegistreringStatusService.hentStartRegistreringStatus(any()) } returns START_REGISTRERING_STATUS
        val expected = FileToJson.toJson("/registrering/startregistrering.json")

        val result = mvc.get("/api/startregistrering")
            .andExpect { status { isOk() } }
            .andReturn().response.contentAsString

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `serialiserer tom registrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
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
    fun `serialiserer ingen igangsatt registrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { hentRegistreringService.hentIgangsattOrdinaerBrukerRegistrering(any()) } returns null
        every { hentRegistreringService.hentSykmeldtRegistrering(any()) } returns null

        val result = mvc.get("/api/igangsattregistrering")
            .andExpect {
                status { isNoContent() }
            }
            .andReturn().response.contentAsString

        assertThat(result).isEqualTo("")
    }

    @Test
    fun `serialiserer igangsatt registrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { hentRegistreringService.hentIgangsattOrdinaerBrukerRegistrering(any()) } returns GYLDIG_BRUKERREGISTRERING
        every { hentRegistreringService.hentSykmeldtRegistrering(any()) } returns null

        val result = mvc.get("/api/igangsattregistrering")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
            }
            .andReturn().response.getContentAsString(StandardCharsets.UTF_8)

        assertThat(result).isEqualTo(REGISTRERING_RESPONSE)
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedHentingAvStartRegistreringsstatus() {
        every { startRegistreringStatusService.hentStartRegistreringStatus(any()) } returns StartRegistreringStatusDto()
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        registreringResource.hentStartRegistreringStatus()
        //verify(exactly = 1) { autorisasjonService.sjekkLesetilgangMedAktorId(any()) }
    }

    @Test
    fun skalFeileVedHentingAvStartRegistreringsstatusMedUgyldigFnr() {
        every { startRegistreringStatusService.hentStartRegistreringStatus(any()) } returns StartRegistreringStatusDto()

        assertThrows<RuntimeException>("Fødselsnummer ikke gyldig.") { registreringResource.hentRegistrering() }
        verify { autorisasjonService wasNot Called }
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedHentingAvRegistrering() {
        every { hentRegistreringService.hentBrukerregistrering(any()) } returns BrukerRegistreringWrapperFactory.create(
            gyldigBrukerRegistrering(), null
        )
        every { hentRegistreringService.hentSykmeldtRegistrering(any()) } returns null
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        registreringResource.hentRegistrering()
        verify(exactly = 1) { autorisasjonService.sjekkLesetilgangTilBruker(IDENTER.finnGjeldendeAktorId()) }
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedRegistreringSykmeldt() {
        val sykmeldtRegistrering = gyldigSykmeldtRegistrering(
            besvarelse = Besvarelse(
                fremtidigSituasjon = FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER,
                tilbakeIArbeid = TilbakeIArbeidSvar.JA_FULL_STILLING,
            )
        )
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        registreringResource.registrerSykmeldt(sykmeldtRegistrering)
        verify(exactly = 1) { autorisasjonService.sjekkSkrivetilgangTilBruker(any<AktorId>()) }
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedRegistreringAvBruker() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            besvarelse = Besvarelse(helseHinder = HelseHinderSvar.NEI)
        )
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every {
            brukerRegistreringService.registrerBrukerUtenOverforing(
                ordinaerBrukerRegistrering,
                Bruker(FoedselsnummerTestdataBuilder.aremark(), AktorId("1234")),
                null
            )
        } returns ordinaerBrukerRegistrering
        registreringResource.registrerBruker(ordinaerBrukerRegistrering)
        verify(exactly = 1) { autorisasjonService.sjekkSkrivetilgangTilBruker(any<AktorId>()) }
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

        private const val REGISTRERING_REQUEST =
            "{\"sisteStilling\":{\"label\":\"Annen stilling\",\"styrk08\":\"-1\",\"konseptId\":-1},\"besvarelse\":{\"sisteStilling\":\"INGEN_SVAR\",\"utdanning\":\"INGEN_UTDANNING\",\"utdanningBestatt\":\"INGEN_SVAR\",\"utdanningGodkjent\":\"INGEN_SVAR\",\"dinSituasjon\":\"MISTET_JOBBEN\",\"helseHinder\":\"NEI\",\"andreForhold\":\"NEI\"},\"teksterForBesvarelse\":[{\"sporsmalId\":\"sisteStilling\",\"sporsmal\":\"Hva er din siste jobb?\",\"svar\":\"Annen stilling\"},{\"sporsmalId\":\"utdanning\",\"sporsmal\":\"Hva er din høyeste fullførte utdanning?\",\"svar\":\"Ingen utdanning\"},{\"sporsmalId\":\"utdanningBestatt\",\"sporsmal\":\"Er utdanningen din bestått?\",\"svar\":\"Ikke aktuelt\"},{\"sporsmalId\":\"utdanningGodkjent\",\"sporsmal\":\"Er utdanningen din godkjent i Norge?\",\"svar\":\"Ikke aktuelt\"},{\"sporsmalId\":\"dinSituasjon\",\"sporsmal\":\"Velg den situasjonen som passer deg best\",\"svar\":\"Har mistet eller kommer til å miste jobben\"},{\"sporsmalId\":\"helseHinder\",\"sporsmal\":\"Har du helseproblemer som hindrer deg i å søke eller være i jobb?\",\"svar\":\"Nei\"},{\"sporsmalId\":\"andreForhold\",\"sporsmal\":\"Har du andre problemer med å søke eller være i jobb?\",\"svar\":\"Nei\"}]}"
    }
}

@Configuration
private class RegistreringResourceConfig {
    @Bean
    fun registreringResource(
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        brukerRegistreringService: BrukerRegistreringService,
        hentRegistreringService: HentRegistreringService,
        unleashClient: UnleashClient,
        sykmeldtRegistreringService: SykmeldtRegistreringService,
        startRegistreringStatusService: StartRegistreringStatusService,
        reaktiveringBrukerService: ReaktiveringBrukerService
    ) = RegistreringResource(
        autorisasjonService,
        userService,
        brukerRegistreringService,
        hentRegistreringService,
        unleashClient,
        sykmeldtRegistreringService,
        startRegistreringStatusService,
        reaktiveringBrukerService,
    )

    @Bean
    fun autorisasjonService(): AutorisasjonService = mockk(relaxed = true)

    @Bean
    fun unleashClient(): UnleashClient = mockk(relaxed = true)

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

    @Bean
    fun sykmeldtRegistreringService(): SykmeldtRegistreringService = mockk(relaxed = true)

    @Bean
    fun inaktivBrukerService(): ReaktiveringBrukerService = mockk(relaxed = true)
}