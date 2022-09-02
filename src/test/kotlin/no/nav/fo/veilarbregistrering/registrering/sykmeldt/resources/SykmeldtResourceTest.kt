package no.nav.fo.veilarbregistrering.registrering.sykmeldt.resources

import io.mockk.*
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringService
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.veileder.NavVeilederService
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

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [SykmeldtResourceConfig::class])
class SykmeldtResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val tilgangskontrollService: TilgangskontrollService,
    @Autowired private val authContextHolder: AuthContextHolder,
    @Autowired private val pdlOppslagGateway: PdlOppslagGateway,
    @Autowired private val sykmeldtResource: SykmeldtResource
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
    fun skalSjekkeTilgangTilBrukerVedRegistreringSykmeldt() {
        val sykmeldtRegistrering = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(
            besvarelse = Besvarelse(
                fremtidigSituasjon = FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER,
                tilbakeIArbeid = TilbakeIArbeidSvar.JA_FULL_STILLING,
            )
        )
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        sykmeldtResource.registrerSykmeldt(sykmeldtRegistrering)
        verify(exactly = 1) { tilgangskontrollService.sjekkSkrivetilgangTilBruker(any()) }
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
private class SykmeldtResourceConfig {
    @Bean
    fun sykmeldtResource(
        tilgangskontrollService: TilgangskontrollService,
        userService: UserService,
        unleashClient: UnleashClient,
        sykmeldtRegistreringService: SykmeldtRegistreringService,
        navVeilederService: NavVeilederService
    ) = SykmeldtResource(
        tilgangskontrollService,
        userService,
        unleashClient,
        sykmeldtRegistreringService,
        navVeilederService
    )

    @Bean
    fun navVeilederService(tilgangskontrollService: TilgangskontrollService, userService: UserService): NavVeilederService {
        return NavVeilederService(tilgangskontrollService, userService)
    }

    @Bean
    fun tilgangskontrollService(): TilgangskontrollService = mockk(relaxed = true)

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
    fun sykmeldtRegistreringService(): SykmeldtRegistreringService = mockk(relaxed = true)
}