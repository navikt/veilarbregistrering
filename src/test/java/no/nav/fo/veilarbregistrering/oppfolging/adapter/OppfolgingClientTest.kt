package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.featuretoggle.UnleashService
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import javax.servlet.http.HttpServletRequest

internal class OppfolgingClientTest {
    private lateinit var inaktivBrukerService: InaktivBrukerService
    private lateinit var oppfolgingClient: OppfolgingClient
    private lateinit var mockServer: ClientAndServer

    @AfterEach
    fun tearDown() {
        mockServer.stop()
    }

    @BeforeEach
    fun setup() {
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT)
        val brukerRegistreringRepository: BrukerRegistreringRepository = mockk(relaxed = true)
        val sykeforloepMetadataClient: SykmeldtInfoClient = mockk()
        val profileringService: ProfileringService = mockk()
        val unleashService: UnleashService = mockk(relaxed = true)
        val autorisasjonService: AutorisasjonService = mockk(relaxed = true)
        val metricsService: MetricsService = mockk(relaxed = true)
        oppfolgingClient = buildClient(metricsService, jacksonObjectMapper().findAndRegisterModules())

        val oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient)
        val brukerTilstandService = BrukerTilstandService(
            oppfolgingGateway,
            SykemeldingService(SykemeldingGatewayImpl(sykeforloepMetadataClient), autorisasjonService, metricsService),
            unleashService
        )
        inaktivBrukerService = InaktivBrukerService(
            brukerTilstandService,
            brukerRegistreringRepository,
            oppfolgingGateway
        )
        every {
            profileringService.profilerBruker(
                any(),
                any(),
                any()
            )
        } returns
                Profilering()
                    .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                    .setAlder(50)
                    .setJobbetSammenhengendeSeksAvTolvSisteManeder(true)

    }

    private fun buildClient(metricsService: MetricsService, findAndRegisterModules: ObjectMapper): OppfolgingClient {
        mockkStatic(RequestContext::class)
        val systemUserTokenProvider: SystemUserTokenProvider = mockk()
        val httpServletRequest: HttpServletRequest = mockk()
        every { RequestContext.servletRequest() } returns httpServletRequest
        every { httpServletRequest.getHeader(any()) } returns ""
        every { systemUserTokenProvider.systemUserToken } returns "testToken"
        val baseUrl = "http://$MOCKSERVER_URL:$MOCKSERVER_PORT"
        return OppfolgingClient(metricsService, findAndRegisterModules, baseUrl, systemUserTokenProvider).also { oppfolgingClient = it }
    }

    @Test
    fun skal_kaste_RuntimeException_ved_vilkaarlig_feil_som_ikke_er_haandtert() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(
            HttpResponse.response().withStatusCode(404)
        )
        Assertions.assertThrows(RuntimeException::class.java) {
            oppfolgingClient.aktiverBruker(
                AktiverBrukerData(
                    FNR,
                    Innsatsgruppe.SITUASJONSBESTEMT_INNSATS
                )
            )
        }
    }

    @Test
    fun skal_returnere_response_ved_204() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(
            HttpResponse.response()
                    .withStatusCode(204)
                    .withBody(okRegistreringBody(), MediaType.JSON_UTF_8)
        )
        Assertions.assertNotNull(
            oppfolgingClient.aktiverBruker(
                AktiverBrukerData(
                    FNR,
                    Innsatsgruppe.SITUASJONSBESTEMT_INNSATS
                )
            )
        )
    }

    @Test
    fun testAtReaktiveringFeilerDersomArenaSierAtBrukerErUnderOppfolging() {
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/oppfolging"))
            .respond(
                HttpResponse.response().withBody(settOppfolgingOgReaktivering(true, false), MediaType.JSON_UTF_8)
                    .withStatusCode(200)
            )
        Assertions.assertThrows(RuntimeException::class.java) { inaktivBrukerService.reaktiverBruker(BRUKER) }
    }

    @Test
    fun testAtReaktiveringGirOKDersomArenaSierAtBrukerKanReaktiveres() {
        mockServer.`when`(HttpRequest.request().withMethod("GET").withPath("/oppfolging"))
            .respond(
                HttpResponse.response().withBody(settOppfolgingOgReaktivering(false, true), MediaType.JSON_UTF_8)
                    .withStatusCode(200)
            )
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/reaktiverbruker")).respond(
            HttpResponse.response().withStatusCode(204)
        )
        inaktivBrukerService.reaktiverBruker(BRUKER)
    }

    @Test
    fun `skal kaste riktig feil ved manglende oppholdstillatelse`() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(
            HttpResponse.response().withBody(FileToJson.toJson("/oppfolging/manglerOppholdstillatelse.json"))
                .withStatusCode(403)
        )
        val exception: AktiverBrukerException = assertThrows {
            oppfolgingClient.aktiverBruker(AktiverBrukerData(FNR, Innsatsgruppe.SITUASJONSBESTEMT_INNSATS))
        }
        assertThat(exception.aktiverBrukerFeil).isEqualTo(AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE)
    }

    @Test
    fun `skal kaste riktig feil dersom bruker ikke kan reaktiveres`() {
        mockServer.`when`(HttpRequest.request().withMethod("POST").withPath("/oppfolging/aktiverbruker")).respond(
            HttpResponse.response().withBody(FileToJson.toJson("/oppfolging/kanIkkeReaktiveres.json"))
                .withStatusCode(403)
        )
        val exception: AktiverBrukerException = assertThrows {
            oppfolgingClient.aktiverBruker(AktiverBrukerData(FNR, Innsatsgruppe.SITUASJONSBESTEMT_INNSATS))
        }
        assertThat(exception.aktiverBrukerFeil).isEqualTo(AktiverBrukerFeil.BRUKER_KAN_IKKE_REAKTIVERES)
    }

    //FIXME: Vurder å flytte disse testene til en ny testklasse med mindre kompleksitet hvor vi tester Client eller
    // Gateway, men ikke alt annet samtidig.
    /*
    @Test
    fun testAtGirInternalServerErrorExceptionDersomBrukerIkkkeHarTilgangTilOppfolgingStatus() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(401));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(BRUKER, null));
    }

    @Test
    fun testAtGirInternalServerErrorExceptionDersomOppfolgingFeiler() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(500));
        assertThrows(InternalServerErrorException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(BRUKER, null));
    }

    @Test
    public void testAtGirWebApplicationExceptionExceptionDersomIngenTilgang() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging")).respond(response().withStatusCode(403));
        assertThrows(WebApplicationException.class, () -> brukerRegistreringService.hentStartRegistreringStatus(BRUKER, null));
    }

    @Test
    fun testAtGirIngenExceptionsDersomKun200OK() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(true, false), MediaType.JSON_UTF_8).withStatusCode(200));

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(BRUKER));
    }

    @Test
    fun testAtGirIngenExceptionsDersomKun200MedKanReaktiveresNull() {
        mockServer.when(request().withMethod("GET").withPath("/oppfolging"))
                .respond(response().withBody(settOppfolgingOgReaktivering(null, null), MediaType.JSON_UTF_8).withStatusCode(200));

        every { arbeidsforholdGateway.hentArbeidsforhold(any()) } returns flereArbeidsforholdTilfeldigSortert());

        assertNotNull(brukerRegistreringService.hentStartRegistreringStatus(BRUKER));
    }
*/
    private fun settOppfolgingOgReaktivering(oppfolging: Boolean, reaktivering: Boolean): String {
        return "{\"kanReaktiveres\": $reaktivering, \"underOppfolging\": $oppfolging}"
    }

    private fun okRegistreringBody(): String {
        return """
            {
            "Status": "STATUS_SUKSESS"
            }
            """.trimIndent()
    }

    companion object {
        private const val MOCKSERVER_URL = "localhost"
        private const val MOCKSERVER_PORT = 1084
        private val IDENT = Foedselsnummer.of("10108000398") //Aremark fiktivt fnr.";
        private val FNR = Fnr("10108000398") //Aremark fiktivt fnr.";
        private val BRUKER = Bruker.of(IDENT, AktorId.of("AKTØRID"))
    }
}