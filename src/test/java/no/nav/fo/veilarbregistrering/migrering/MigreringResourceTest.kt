package no.nav.fo.veilarbregistrering.migrering

import io.mockk.*
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar
import no.nav.fo.veilarbregistrering.besvarelse.HelseHinderSvar
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.db.migrering.MigreringRepositoryImpl
import no.nav.fo.veilarbregistrering.db.migrering.MigreringResource
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
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
import javax.servlet.http.HttpServletRequest

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [MigreringResourceConfig::class])
class RegistreringResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val migreringResource: MigreringResource,
) {
    private lateinit var request: HttpServletRequest

    @BeforeEach
    fun setup() {
        clearAllMocks()
        mockkStatic(RequestContext::class)
        request = mockk(relaxed = true)
        every { RequestContext.servletRequest() } returns request
    }

    @Test
    fun `hent oppdaterte statuser krever post`() {


        val responseString = mvc.get("/api/migrering/registrering-tilstand/hent-oppdaterte-statuser") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ 1: "MOTTATT" }"""
        }.andExpect {
            status { isMethodNotAllowed }
        }.andReturn().response.contentAsString

        assertThat(responseString).isNullOrEmpty()
    }
/*
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
    fun `Kan parse registrering json i requestbody til objekt ok`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        val responseString = mvc.post("/api/startregistrering") {
            contentType = MediaType.APPLICATION_JSON
            content = REGISTRERING_REQUEST
        }.andExpect {
            status { isOk }
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
            .andExpect { status { isOk } }
            .andReturn().response.contentAsString

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `serialiserer tom registrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { hentRegistreringService.hentOrdinaerBrukerRegistrering(any()) } returns null
        every { hentRegistreringService.hentSykmeldtRegistrering(any()) } returns null

        val result = mvc.get("/api/registrering")
            .andExpect {
                status { isNoContent }
            }
            .andReturn().response.contentAsString

        assertThat(result).isEqualTo("")
    }

    @Test
    fun `serialiserer registrering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { hentRegistreringService.hentOrdinaerBrukerRegistrering(any()) } returns GYLDIG_BRUKERREGISTRERING
        every { hentRegistreringService.hentSykmeldtRegistrering(any()) } returns null

        val result = mvc.get("/api/registrering")
            .andExpect {
                status { isOk }
                content { contentType("application/json") }
            }
            .andReturn().response.getContentAsString(StandardCharsets.UTF_8)

        assertThat(result).isEqualTo(REGISTRERING_RESPONSE)
    }

    @Test
    fun `serialiserer registrering med profilering riktig`() {
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        every { hentRegistreringService.hentOrdinaerBrukerRegistrering(any()) } returns GYLDIG_BRUKERREGISTRERING_M_PROF
        every { hentRegistreringService.hentSykmeldtRegistrering(any()) } returns null

        val result = mvc.get("/api/registrering")
            .andExpect {
                status { isOk }
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
                status { isNoContent }
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
                status { isOk }
                content { contentType("application/json") }
            }
            .andReturn().response.getContentAsString(StandardCharsets.UTF_8)

        assertThat(result).isEqualTo(REGISTRERING_RESPONSE)
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
                gyldigBrukerRegistrering()
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
                Besvarelse(
                    fremtidigSituasjon = FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER,
                    tilbakeIArbeid = TilbakeIArbeidSvar.JA_FULL_STILLING,
                )
            )
        every { request.getParameter("fnr") } returns IDENT.stringValue()
        every { pdlOppslagGateway.hentIdenter(any<Foedselsnummer>()) } returns IDENTER
        registreringResource.registrerSykmeldt(sykmeldtRegistrering)
        verify(exactly = 1) { autorisasjonService.sjekkSkrivetilgangMedAktorId(any()) }
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
                Bruker.of(FoedselsnummerTestdataBuilder.aremark(), AktorId("1234")),
                null
            )
        } returns ordinaerBrukerRegistrering
        registreringResource.registrerBruker(ordinaerBrukerRegistrering)
        verify(exactly = 1) { autorisasjonService.sjekkSkrivetilgangMedAktorId(any()) }
    }*/

}
@Configuration
private class MigreringResourceConfig {
    @Bean
    fun migreringResource(
        migreringRepositoryImpl: MigreringRepositoryImpl,
            brukerRegistreringRepository: BrukerRegistreringRepository,
            registreringTilstandRepository: RegistreringTilstandRepository,
            ) = MigreringResource(
        migreringRepositoryImpl,
        brukerRegistreringRepository,
        registreringTilstandRepository,
    )
    @Bean
    fun migreringRepositoryImpl(): MigreringRepositoryImpl = mockk(relaxed = true)

    @Bean
    fun brukerRegistreringRepository(): BrukerRegistreringRepository = mockk(relaxed = true)

    @Bean
    fun registreringTilstandRepository(): RegistreringTilstandRepository = mockk(relaxed = true)

}