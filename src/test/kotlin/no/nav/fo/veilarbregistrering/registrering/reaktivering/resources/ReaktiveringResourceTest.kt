package no.nav.fo.veilarbregistrering.registrering.reaktivering.resources

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringBrukerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.*
import javax.servlet.http.HttpServletRequest

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [ReaktiveringResourceConfig::class])
class ReaktiveringResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val autorisasjonService: AutorisasjonService,
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
        every { autorisasjonService.erVeileder() } returns true
        every { authContextHolder.subject} returns Optional.of("sub")
        every { authContextHolder.idTokenClaims } returns Optional.empty()
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
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        unleashClient: UnleashClient,
        reaktiveringBrukerService: ReaktiveringBrukerService
    ) = ReaktiveringResource(
        autorisasjonService,
        userService,
        unleashClient,
        reaktiveringBrukerService,
    )

    @Bean
    fun autorisasjonService(): AutorisasjonService = mockk(relaxed = true)

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
    fun reaktiveringBrukerService(): ReaktiveringBrukerService = mockk(relaxed = true)
}