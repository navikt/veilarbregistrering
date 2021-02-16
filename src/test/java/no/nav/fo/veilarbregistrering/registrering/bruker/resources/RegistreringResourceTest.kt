package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import io.mockk.*
import no.nav.common.featuretoggle.UnleashService
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar
import no.nav.fo.veilarbregistrering.besvarelse.HelseHinderSvar
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService
import no.nav.fo.veilarbregistrering.registrering.bruker.*
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
import javax.servlet.http.HttpServletRequest

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [RegistreringResourceConfig::class])
class RegistreringResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val registreringResource: RegistreringResource,
    @Autowired private val autorisasjonService: AutorisasjonService,
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
    }

    @Test
    fun `startregistrersykmeldt har riktig status og responsbody`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        val responseString = mvc.post("/api/startregistrersykmeldt") {
            contentType = MediaType.APPLICATION_JSON
            content = FileToJson.toJson("/registrering/startregistrersykmeldt.json")
        }.andExpect {
            status { isNoContent }
        }.andReturn().response.contentAsString

        assertThat(responseString).isNullOrEmpty()
    }

    @Test
    fun `startreaktivering returnerer riktig status og responsbody`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        val responseString = mvc.post("/api/startreaktivering").andExpect {
            status { isNoContent }
        }.andReturn().response.contentAsString

        assertThat(responseString).isNullOrEmpty()
    }

    @Test
    fun `serialiserer startregistrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { startRegistreringStatusService.hentStartRegistreringStatus(any()) } returns START_REGISTRERING_STATUS
        val expected = FileToJson.toJson("/registrering/startregistrering.json")

        val result = mvc.get("/api/startregistrering")
            .andExpect { status { isOk } }
            .andReturn().response.contentAsString

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedHentingAvStartRegistreringsstatus() {
        mockkStatic(StartRegistreringStatusMetrikker::class)
        every { StartRegistreringStatusMetrikker.rapporterRegistreringsstatus(any(), any()) } just runs
        every {startRegistreringStatusService.hentStartRegistreringStatus(any()) } returns StartRegistreringStatusDto()
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        registreringResource.hentStartRegistreringStatus()
        verify(exactly = 1) { autorisasjonService.sjekkLesetilgangMedAktorId(any()) }
    }

    @Test
    fun skalFeileVedHentingAvStartRegistreringsstatusMedUgyldigFnr() {
        every {startRegistreringStatusService.hentStartRegistreringStatus(any()) } returns StartRegistreringStatusDto()

        assertThrows<RuntimeException>("Fødselsnummer ikke gyldig.")  { registreringResource.hentRegistrering() }
        verify { autorisasjonService wasNot Called }
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedHentingAvRegistrering() {
        every { hentRegistreringService.hentOrdinaerBrukerRegistrering(any()) } returns
                OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()
        every { hentRegistreringService.hentSykmeldtRegistrering(any()) } returns null
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        registreringResource.hentRegistrering()
        verify(exactly = 1) {autorisasjonService.sjekkLesetilgangMedAktorId(IDENTER.finnGjeldendeAktorId()) }
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedRegistreringSykmeldt() {
        val sykmeldtRegistrering = SykmeldtRegistrering()
            .setBesvarelse(
                Besvarelse()
                    .setFremtidigSituasjon(FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER)
                    .setTilbakeIArbeid(TilbakeIArbeidSvar.JA_FULL_STILLING)
            )
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        registreringResource.registrerSykmeldt(sykmeldtRegistrering)
        verify(exactly = 1) { autorisasjonService.sjekkSkrivetilgangMedAktorId(any()) }
    }

    @Test
    fun skalSjekkeTilgangTilBrukerVedRegistreringAvBruker() {
        val ordinaerBrukerRegistrering = OrdinaerBrukerRegistrering()
            .setBesvarelse(Besvarelse().setHelseHinder(HelseHinderSvar.NEI)).setId(2L)
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every {
            brukerRegistreringService.registrerBrukerUtenOverforing(
                ordinaerBrukerRegistrering,
                Bruker.of(FoedselsnummerTestdataBuilder.aremark(), AktorId.of("1234")),
                null
            )
        } returns ordinaerBrukerRegistrering
        registreringResource.registrerBruker(ordinaerBrukerRegistrering)
        verify(exactly = 1) { autorisasjonService.sjekkSkrivetilgangMedAktorId(any()) }
    }

    companion object {
        private val IDENT = Foedselsnummer.of("10108000398") //Aremark fiktivt fnr.";
        private val IDENTER = Identer.of(
            mutableListOf(
                Ident(IDENT.stringValue(), false, Gruppe.FOLKEREGISTERIDENT),
                Ident("22222222222", false, Gruppe.AKTORID)
            )
        )
        private val START_REGISTRERING_STATUS = StartRegistreringStatusDto()
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
            unleashService: UnleashService,
            sykmeldtRegistreringService: SykmeldtRegistreringService,
            startRegistreringStatusService: StartRegistreringStatusService,
            inaktivBrukerService: InaktivBrukerService,
            influxMetricsService: InfluxMetricsService,
    ) = RegistreringResource(
        autorisasjonService,
        userService,
        brukerRegistreringService,
        hentRegistreringService,
        unleashService,
        sykmeldtRegistreringService,
        startRegistreringStatusService,
        inaktivBrukerService,
        influxMetricsService,
    )
    @Bean
    fun autorisasjonService(): AutorisasjonService = mockk(relaxed = true)
    @Bean
    fun unleashService(): UnleashService = mockk(relaxed = true)
    @Bean
    fun metricsService(): InfluxMetricsService = mockk(relaxed = true)
    @Bean
    fun pdlOppslagGateway(): PdlOppslagGateway = mockk()
    @Bean
    fun brukerRegistreringService(): BrukerRegistreringService = mockk(relaxed = true)
    @Bean
    fun hentRegistreringService(): HentRegistreringService = mockk()
    @Bean
    fun startRegistreringStatusService(): StartRegistreringStatusService = mockk()
    @Bean
    fun userService(pdlOppslagGateway: PdlOppslagGateway): UserService = UserService(pdlOppslagGateway)
    @Bean
    fun sykmeldtRegistreringService(): SykmeldtRegistreringService = mockk(relaxed = true)
    @Bean
    fun inaktivBrukerService(): InaktivBrukerService = mockk(relaxed = true)
}