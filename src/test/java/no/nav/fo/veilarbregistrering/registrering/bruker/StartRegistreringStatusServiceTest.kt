package no.nav.fo.veilarbregistrering.registrering.bruker

import io.mockk.MockKAdditionalAnswerScope
import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.adapter.ErUnderOppfolgingDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class StartRegistreringStatusServiceTest {
    private lateinit var brukerRegistreringService: StartRegistreringStatusService
    private lateinit var arbeidsforholdGateway: ArbeidsforholdGateway
    private lateinit var oppfolgingClient: OppfolgingClient
    private lateinit var pdlOppslagGateway: PdlOppslagGateway
    @BeforeEach
    fun setup() {
        arbeidsforholdGateway = mockk()
        oppfolgingClient = mockk()
        pdlOppslagGateway = mockk()
        val metricsService: PrometheusMetricsService = mockk(relaxed = true)
        val oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient, mockk(relaxed = true))
        brukerRegistreringService = StartRegistreringStatusService(
            arbeidsforholdGateway,
            BrukerTilstandService(oppfolgingGateway, mockk(relaxed = true)),
            pdlOppslagGateway,
            metricsService
        )
    }

    @Test
    fun skalReturnereUnderOppfolgingNaarUnderOppfolging() {
        mockArbeidssokerSomHarAktivOppfolging()
        val startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus.registreringType == RegistreringType.ALLEREDE_REGISTRERT).isTrue
    }

    @Test
    fun skalReturnereAtBrukerOppfyllerBetingelseOmArbeidserfaring() {
        mockInaktivBrukerUtenReaktivering()
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring()
        val startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus.jobbetSeksAvTolvSisteManeder).isTrue
    }

    @Test
    fun skalReturnereFalseOmIkkeUnderOppfolging() {
        mockOppfolgingMedRespons(inaktivBruker())
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav())
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus.registreringType == RegistreringType.ALLEREDE_REGISTRERT).isFalse
    }

    @Test
    fun skalReturnereAlleredeUnderOppfolging() {
        mockArbeidssokerSomHarAktivOppfolging()
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus.registreringType == RegistreringType.ALLEREDE_REGISTRERT).isTrue
    }

    @Test
    fun skalReturnereReaktivering() {
        mockOppfolgingMedRespons(inaktivBruker())
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav())
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus.registreringType).isEqualTo(RegistreringType.REAKTIVERING)
    }

    @Test
    fun skalReturnereSykmeldtRegistrering() {
        mockSykmeldtBruker()
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus.registreringType)
            .isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }

    @Test
    fun gitt_at_geografiskTilknytning_ikke_ble_funnet_skal_null_returneres() {
        mockInaktivBrukerUtenReaktivering()
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring()
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus).isNotNull
        Assertions.assertThat(startRegistreringStatus.geografiskTilknytning).isNull()
    }

    @Test
    fun gitt_at_geografiskTilknytning_er_1234_skal_1234_returneres() {
        mockInaktivBrukerUtenReaktivering()
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring()
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns
                GeografiskTilknytning("1234")
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus).isNotNull
        Assertions.assertThat(startRegistreringStatus.geografiskTilknytning).isEqualTo("1234")
    }

    @Test
    fun gitt_at_geografiskTilknytning_kaster_exception_skal_null_returneres() {
        mockInaktivBrukerUtenReaktivering()
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring()
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } throws RuntimeException("Ikke tilgang")
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus).isNotNull
        Assertions.assertThat(startRegistreringStatus.geografiskTilknytning).isNull()
    }

    @Test
    fun skalReturnereOrdinarRegistrering() {
        mockIkkeSykmeldtBruker()
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav())
        val startRegistreringStatus = getStartRegistreringStatus()
        Assertions.assertThat(startRegistreringStatus.registreringType == RegistreringType.ORDINAER_REGISTRERING).isTrue
    }

    private fun getStartRegistreringStatus(bruker: Bruker = BRUKER_INTERN): StartRegistreringStatusDto {
        return brukerRegistreringService.hentStartRegistreringStatus(bruker)
    }

    private fun arbeidsforholdSomOppfyllerKrav(): List<Arbeidsforhold> {
        return listOf(
            Arbeidsforhold(
                "orgnummer", "styrk", LocalDate.of(2017, 1, 10), null, null
            )
        )
    }

    private fun inaktivBruker(): OppfolgingStatusData {
        return OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(true)
    }

    private fun mockArbeidssokerSomHarAktivOppfolging() {
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
            OppfolgingStatusData().withUnderOppfolging(true).withKanReaktiveres(false)
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(true)
    }



    private fun mockSykmeldtBruker() {
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
                OppfolgingStatusData()
                    .withUnderOppfolging(false)
                    .withKanReaktiveres(false)
                    .withErSykmeldtMedArbeidsgiver(true)
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(true)
    }

    private fun mockIkkeSykmeldtBruker() {
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
                OppfolgingStatusData()
                    .withUnderOppfolging(false)
                    .withKanReaktiveres(false)
                    .withErSykmeldtMedArbeidsgiver(false)
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(true)
    }

    private fun mockArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>) =
        every { arbeidsforholdGateway.hentArbeidsforhold(any()) } returns FlereArbeidsforhold(arbeidsforhold)

    private fun mockInaktivBrukerUtenReaktivering() {
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
                OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(false)
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(true)
    }

    private fun mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring() =
        every { arbeidsforholdGateway.hentArbeidsforhold(any()) } returns
            FlereArbeidsforhold(
                listOf(
                    Arbeidsforhold(
                        "orgnummer",
                        "styrk",
                        LocalDate.of(2017, 1, 10),
                        null,
                        null
                    )
                )
            )

    private fun mockOppfolgingMedRespons(oppfolgingStatusData: OppfolgingStatusData) {
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns oppfolgingStatusData
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(true)
    }

    companion object {
        private val FNR_OPPFYLLER_KRAV =
            FoedselsnummerTestdataBuilder.fodselsnummerOnDateMinusYears(LocalDate.now(), 40)
        private val BRUKER_INTERN = Bruker(FNR_OPPFYLLER_KRAV, AktorId("AKTØRID"))
    }
}