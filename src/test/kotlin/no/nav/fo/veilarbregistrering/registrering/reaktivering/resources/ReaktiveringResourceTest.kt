package no.nav.fo.veilarbregistrering.registrering.reaktivering.resources

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.resources.Fnr
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringBrukerService
import org.assertj.core.api.Assertions
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
import kotlin.test.assertNotNull

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [ReaktiveringResourceConfig::class])
class ReaktiveringResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val tilgangskontrollService: TilgangskontrollService,
    @Autowired private val authContextHolder: AuthContextHolder,
    @Autowired private val pdlOppslagGateway: PdlOppslagGateway,
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
    fun `startreaktivering returnerer riktig status og responsbody`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        val responseString = mvc.post("/api/startreaktivering").andExpect {
            status { isNoContent() }
        }.andReturn().response.contentAsString

        Assertions.assertThat(responseString).isNullOrEmpty()
    }

    @Test
    fun `fullfoerreaktivering returnerer riktig status og responsbody`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        val responseString = mvc.post("/api/fullfoerreaktivering").andExpect {
            status { isNoContent() }
        }.andReturn().response.contentAsString

        Assertions.assertThat(responseString).isNullOrEmpty()
    }

    @Test
    fun `fullfoerreaktivering for systembruker returnerer riktig status og responsbody`() {
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { tilgangskontrollService.erVeileder() } returns false
        every { authContextHolder.erSystemBruker() } returns true

        val responseString = mvc.post("/api/fullfoerreaktivering/systembruker") {
            content = objectMapper.writeValueAsString(Fnr(IDENT.foedselsnummer))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }.andReturn().response.contentAsString

        Assertions.assertThat(responseString).isNullOrEmpty()
    }

    @Test
    fun `kanReaktiveres for systembruker returnerer riktig status og responsbody`() {
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { tilgangskontrollService.erVeileder() } returns false
        every { authContextHolder.erSystemBruker() } returns true

        val response = mvc.post("/api/kan-reaktiveres") {
            content = objectMapper.writeValueAsString(Fnr(IDENT.foedselsnummer))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        assertNotNull(response)
    }

    companion object {
        private val IDENT = Foedselsnummer("10108000398") //Aremark fiktivt fnr.";
        private val IDENTER = Identer(
            mutableListOf(
                Ident(IDENT.stringValue(), false, Gruppe.FOLKEREGISTERIDENT),
                Ident("22222222222", false, Gruppe.AKTORID)
            )
        )
    }
}

@Configuration
private class ReaktiveringResourceConfig {
    @Bean
    fun reaktiveringResource(
        userService: UserService,
        unleashClient: UnleashClient,
        tilgangskontrollService: TilgangskontrollService,
        reaktiveringBrukerService: ReaktiveringBrukerService
    ) = ReaktiveringResource(
        userService,
        unleashClient,
        tilgangskontrollService,
        reaktiveringBrukerService,
    )

    @Bean
    fun unleashClient(): UnleashClient = mockk(relaxed = true)

    @Bean
    fun pdlOppslagGateway(): PdlOppslagGateway = mockk()

    @Bean
    fun authContextHolder(): AuthContextHolder = mockk()

    @Bean
    fun userService(pdlOppslagGateway: PdlOppslagGateway, authContextHolder: AuthContextHolder): UserService =
        UserService(pdlOppslagGateway, authContextHolder)

    @Bean
    fun tilgangskontrollService(): TilgangskontrollService = mockk(relaxed = true)

    @Bean
    fun reaktiveringBrukerService(): ReaktiveringBrukerService = mockk(relaxed = true)
}