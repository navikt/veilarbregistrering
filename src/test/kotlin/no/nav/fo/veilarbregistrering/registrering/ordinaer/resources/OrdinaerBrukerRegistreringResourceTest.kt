package no.nav.fo.veilarbregistrering.registrering.ordinaer.resources

import io.mockk.*
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.HelseHinderSvar
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.veileder.NavVeilederService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import javax.servlet.http.HttpServletRequest

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [OrdinaerBrukerRegistreringResourceConfig::class])
class OrdinaerBrukerRegistreringResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val ordinaerBrukerRegistreringResource: OrdinaerBrukerRegistreringResource,
    @Autowired private val tilgangskontrollService: TilgangskontrollService,
    @Autowired private val authContextHolder: AuthContextHolder,
    @Autowired private val pdlOppslagGateway: PdlOppslagGateway,
    @Autowired private val brukerRegistreringService: BrukerRegistreringService
) {
    private lateinit var request: HttpServletRequest

    @BeforeEach
    fun setup() {
        clearAllMocks()
        mockkStatic(RequestContext::class)
        request = mockk(relaxed = true)
        every { RequestContext.servletRequest() } returns request
        every { tilgangskontrollService.erVeileder() } returns true
        every { authContextHolder.erEksternBruker() } returns false
    }

    @Test
    fun `Fullfoer ordinaer registrering ok`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        val responseString = mvc.post("/api/fullfoerordinaerregistrering") {
            contentType = MediaType.APPLICATION_JSON
            content = REGISTRERING_REQUEST
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        println(responseString)
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedRegistreringAvBruker() {
        val ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
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
        ordinaerBrukerRegistreringResource.registrerBruker(ordinaerBrukerRegistrering)
        verify(exactly = 1) { tilgangskontrollService.sjekkSkrivetilgangTilBruker(any(), any()) }
    }

    companion object {
        private val IDENT = Foedselsnummer("10108000398") //Aremark fiktivt fnr.";
        private val IDENTER = Identer(
            mutableListOf(
                Ident(IDENT.stringValue(), false, Gruppe.FOLKEREGISTERIDENT),
                Ident("22222222222", false, Gruppe.AKTORID)
            )
        )

        private const val REGISTRERING_REQUEST =
            "{\"sisteStilling\":{\"label\":\"Annen stilling\",\"styrk08\":\"-1\",\"konseptId\":-1},\"besvarelse\":{\"sisteStilling\":\"INGEN_SVAR\",\"utdanning\":\"INGEN_UTDANNING\",\"utdanningBestatt\":\"INGEN_SVAR\",\"utdanningGodkjent\":\"INGEN_SVAR\",\"dinSituasjon\":\"MISTET_JOBBEN\",\"helseHinder\":\"NEI\",\"andreForhold\":\"NEI\"},\"teksterForBesvarelse\":[{\"sporsmalId\":\"sisteStilling\",\"sporsmal\":\"Hva er din siste jobb?\",\"svar\":\"Annen stilling\"},{\"sporsmalId\":\"utdanning\",\"sporsmal\":\"Hva er din høyeste fullførte utdanning?\",\"svar\":\"Ingen utdanning\"},{\"sporsmalId\":\"utdanningBestatt\",\"sporsmal\":\"Er utdanningen din bestått?\",\"svar\":\"Ikke aktuelt\"},{\"sporsmalId\":\"utdanningGodkjent\",\"sporsmal\":\"Er utdanningen din godkjent i Norge?\",\"svar\":\"Ikke aktuelt\"},{\"sporsmalId\":\"dinSituasjon\",\"sporsmal\":\"Velg den situasjonen som passer deg best\",\"svar\":\"Har mistet eller kommer til å miste jobben\"},{\"sporsmalId\":\"helseHinder\",\"sporsmal\":\"Har du helseproblemer som hindrer deg i å søke eller være i jobb?\",\"svar\":\"Nei\"},{\"sporsmalId\":\"andreForhold\",\"sporsmal\":\"Har du andre problemer med å søke eller være i jobb?\",\"svar\":\"Nei\"}]}"
    }
}

@Configuration
private class OrdinaerBrukerRegistreringResourceConfig {
    @Bean
    fun ordinaerBrukerRegistreringResource(
        tilgangskontrollService: TilgangskontrollService,
        userService: UserService,
        brukerRegistreringService: BrukerRegistreringService,
        navVeilederService: NavVeilederService,
        unleashClient: UnleashClient
    ) = OrdinaerBrukerRegistreringResource(
        tilgangskontrollService,
        userService,
        brukerRegistreringService,
        navVeilederService,
        unleashClient
    )

    @Bean
    fun tilgangskontrollService(): TilgangskontrollService = mockk(relaxed = true)

    @Bean
    fun navVeilederService(tilgangskontrollService: TilgangskontrollService, userService: UserService): NavVeilederService {
        return NavVeilederService(tilgangskontrollService, userService)
    }

    @Bean
    fun unleashClient(): UnleashClient = mockk(relaxed = true)

    @Bean
    fun pdlOppslagGateway(): PdlOppslagGateway = mockk()

    @Bean
    fun brukerRegistreringService(): BrukerRegistreringService = mockk(relaxed = true)

    @Bean
    fun authContextHolder(): AuthContextHolder = mockk()

    @Bean
    fun userService(pdlOppslagGateway: PdlOppslagGateway, authContextHolder: AuthContextHolder): UserService =
        UserService(pdlOppslagGateway, authContextHolder)
}