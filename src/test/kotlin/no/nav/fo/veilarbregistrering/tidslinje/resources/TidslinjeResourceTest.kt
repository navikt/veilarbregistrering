package no.nav.fo.veilarbregistrering.tidslinje.resources

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.autorisasjon.DefaultAutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.tidslinje.TidslinjeAggregator
import no.nav.fo.veilarbregistrering.tidslinje.TidslinjeTestdataBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.*
import java.util.Collections.emptyList
import javax.servlet.http.HttpServletRequest

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [TidslinjeResourceConfig::class])
class TidslinjeResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val autorisasjonService: DefaultAutorisasjonService,
    @Autowired private val authContextHolder: AuthContextHolder,
    @Autowired private val pdlOppslagGateway: PdlOppslagGateway,
    @Autowired private val tidslinjeAggregator: TidslinjeAggregator) {

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
    fun `person uten data skal gi 200 og tom liste med historiske elementer`() {
        every { request.getParameter("fnr") } returns aremark().stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { tidslinjeAggregator.tidslinje(any()) } returns emptyList()

        val resultat = mvc.get("/api/tidslinje")
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        assertThat(resultat).isEqualTo(TOM_LISTE)
    }

    @Test
    fun `person med ordinær registrering skal gi 200 og en liste med historiske elementer`() {
        every { request.getParameter("fnr") } returns aremark().stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { tidslinjeAggregator.tidslinje(any()) } returns TidslinjeTestdataBuilder.default().tidslinje()

        val resultat = mvc.get("/api/tidslinje")
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        assertThat(resultat).isEqualTo(LISTE)
    }

    companion object {
        private val IDENTER = Identer(
                mutableListOf(
                        Ident(aremark().stringValue(), false, Gruppe.FOLKEREGISTERIDENT),
                        Ident("22222222222", false, Gruppe.AKTORID)
                )
        )
        private val TOM_LISTE = "{\"historiskeElementer\":[]}"
        private val LISTE = "{\"historiskeElementer\":[{\"periode\":{\"fraOgMedDato\":\"2021-04-11\",\"tilOgMedDato\":null},\"kilde\":\"ARBEIDSSOKERREGISTRERING\",\"type\":\"ORDINAER_REGISTRERING\"}]}"
    }
}

@Configuration
private open class TidslinjeResourceConfig {

    @Bean
    fun tidslinjeResource(
        autorisasjonService: DefaultAutorisasjonService,
        userService: UserService,
        tidslinjeAggregator: TidslinjeAggregator
    ) = TidslinjeResource(autorisasjonService, userService, tidslinjeAggregator)

    @Bean
    fun autorisasjonService(): DefaultAutorisasjonService = mockk(relaxed = true)

    @Bean
    fun pdlOppslagGateway(): PdlOppslagGateway = mockk()

    @Bean
    fun authContextHolder(): AuthContextHolder = mockk()

    @Bean
    fun userService(pdlOppslagGateway: PdlOppslagGateway, authContextHolder: AuthContextHolder): UserService = UserService(
        pdlOppslagGateway,
        authContextHolder
    )

    @Bean
    fun tidslinjeAggregator(): TidslinjeAggregator = mockk()
}
