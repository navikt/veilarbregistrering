package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import io.mockk.*
import no.nav.common.featuretoggle.UnleashService
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar
import no.nav.fo.veilarbregistrering.besvarelse.HelseHinderSvar
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.servlet.http.HttpServletRequest

class RegistreringResourceTest {
    private lateinit var autorisasjonService: AutorisasjonService
    private lateinit var registreringResource: RegistreringResource
    private lateinit var userService: UserService
    private lateinit var pdlOppslagGateway: PdlOppslagGateway
    private lateinit var brukerRegistreringService: BrukerRegistreringService
    private lateinit var hentRegistreringService: HentRegistreringService
    private lateinit var startRegistreringStatusService: StartRegistreringStatusService
    private lateinit var request: HttpServletRequest

    @BeforeEach
    fun setup() {
        clearAllMocks()
        mockkStatic(RequestContext::class)
        autorisasjonService = mockk(relaxed = true)
        pdlOppslagGateway = mockk()
        brukerRegistreringService = mockk(relaxed = true)
        hentRegistreringService = mockk()
        startRegistreringStatusService = mockk()
        userService = UserService(pdlOppslagGateway)
        val sykmeldtRegistreringService: SykmeldtRegistreringService = mockk(relaxed = true)
        val inaktivBrukerService: InaktivBrukerService = mockk()
        val unleashService: UnleashService = mockk(relaxed = true)
        val metricsService: MetricsService = mockk(relaxed = true)
        request = mockk(relaxed = true)
        registreringResource = RegistreringResource(
            autorisasjonService,
            userService,
            brukerRegistreringService,
            hentRegistreringService,
            unleashService,
            sykmeldtRegistreringService,
            startRegistreringStatusService,
            inaktivBrukerService,
            metricsService
        )
        every { autorisasjonService.erVeileder() } returns true
        every { RequestContext.servletRequest() } returns request
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
        private val IDENTER = Identer.of(mutableListOf(
            Ident(IDENT.stringValue(), false, Gruppe.FOLKEREGISTERIDENT),
            Ident("22222222222", false, Gruppe.AKTORID)
        ))
    }
}