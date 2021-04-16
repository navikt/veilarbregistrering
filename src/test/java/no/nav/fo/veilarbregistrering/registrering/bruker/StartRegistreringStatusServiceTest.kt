package no.nav.fo.veilarbregistrering.registrering.bruker

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDto
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class StartRegistreringStatusServiceTest {
    private lateinit var brukerRegistreringService: StartRegistreringStatusService
    private lateinit var arbeidsforholdGateway: ArbeidsforholdGateway
    private lateinit var sykeforloepMetadataClient: SykmeldtInfoClient
    private lateinit var oppfolgingClient: OppfolgingClient
    private lateinit var personGateway: PersonGateway
    @BeforeEach
    fun setup() {
        arbeidsforholdGateway = mockk()
        oppfolgingClient = mockk()
        sykeforloepMetadataClient = mockk()
        personGateway = mockk()
        val influxMetricsService: InfluxMetricsService = mockk(relaxed = true)
        val autorisasjonService: AutorisasjonService = mockk(relaxed = true)
        val unleashService: UnleashClient = mockk(relaxed = true)

        val oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient)
        val sykemeldingService =
            SykemeldingService(SykemeldingGatewayImpl(sykeforloepMetadataClient), autorisasjonService, influxMetricsService)
        brukerRegistreringService = StartRegistreringStatusService(
            arbeidsforholdGateway,
            BrukerTilstandService(oppfolgingGateway, sykemeldingService, unleashService, mockk(relaxed = true)),
            personGateway,
            influxMetricsService
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
        mockSykmeldtBrukerOver39uker()
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus.registreringType)
            .isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }

    @Test
    fun skalReturnereSperret() {
        mockSykmeldtBruker()
        mockSykmeldtBrukerUnder39uker()
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus.registreringType).isEqualTo(RegistreringType.SPERRET)
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
        every { personGateway.hentGeografiskTilknytning(any()) } returns
                Optional.of(GeografiskTilknytning.of("1234"))
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus).isNotNull
        Assertions.assertThat(startRegistreringStatus.geografiskTilknytning).isEqualTo("1234")
    }

    @Test
    fun gitt_at_geografiskTilknytning_kaster_exception_skal_null_returneres() {
        mockInaktivBrukerUtenReaktivering()
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring()
        every { personGateway.hentGeografiskTilknytning(any()) } throws RuntimeException("Ikke tilgang")
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus).isNotNull
        Assertions.assertThat(startRegistreringStatus.geografiskTilknytning).isNull()
    }

    @Test
    fun skalReturnereOrdinarRegistrering() {
        mockIkkeSykmeldtBruker()
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav())
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        Assertions.assertThat(startRegistreringStatus.registreringType == RegistreringType.ORDINAER_REGISTRERING).isTrue
    }

    @Test
    fun mockDataSkalIkkeGjeldeNaarMockToggleErAv() {
        mockSykmeldtBruker()
        mockSykmeldtBrukerUnder39uker()
        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)
        verify(exactly = 1) { sykeforloepMetadataClient.hentSykmeldtInfoData(any()) }
        Assertions.assertThat(RegistreringType.SYKMELDT_REGISTRERING == startRegistreringStatus.registreringType).isFalse
    }

    private fun getStartRegistreringStatus(bruker: Bruker): StartRegistreringStatusDto {
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

    private fun mockArbeidssokerSomHarAktivOppfolging() =
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
            OppfolgingStatusData().withUnderOppfolging(true).withKanReaktiveres(false)



    private fun mockSykmeldtBruker() =
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
            OppfolgingStatusData()
                .withUnderOppfolging(false)
                .withKanReaktiveres(false)
                .withErSykmeldtMedArbeidsgiver(true)

    private fun mockIkkeSykmeldtBruker() =
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
            OppfolgingStatusData()
                .withUnderOppfolging(false)
                .withKanReaktiveres(false)
                .withErSykmeldtMedArbeidsgiver(false)

    private fun mockSykmeldtBrukerUnder39uker() {
        val dagensDatoMinus14Uker = LocalDate.now().plusWeeks(14).toString()
        every { sykeforloepMetadataClient.hentSykmeldtInfoData(any()) } returns
                InfotrygdData()
                    .withMaksDato(dagensDatoMinus14Uker)
    }

    private fun mockArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>) =
        every { arbeidsforholdGateway.hentArbeidsforhold(any()) } returns FlereArbeidsforhold.of(arbeidsforhold)

    private fun mockInaktivBrukerUtenReaktivering() =
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns
            OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(false)

    private fun mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring() =
        every { arbeidsforholdGateway.hentArbeidsforhold(any()) } returns
            FlereArbeidsforhold.of(
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

    private fun mockOppfolgingMedRespons(oppfolgingStatusData: OppfolgingStatusData) =
        every { oppfolgingClient.hentOppfolgingsstatus(any()) } returns oppfolgingStatusData

    private fun mockSykmeldtBrukerOver39uker() {
        val dagensDatoMinus13Uker = LocalDate.now().plusWeeks(13).toString()
        every { sykeforloepMetadataClient.hentSykmeldtInfoData(any()) } returns
            InfotrygdData()
                .withMaksDato(dagensDatoMinus13Uker)

    }

    companion object {
        private val FNR_OPPFYLLER_KRAV =
            FoedselsnummerTestdataBuilder.fodselsnummerOnDateMinusYears(LocalDate.now(), 40)
        private val BRUKER_INTERN = Bruker.of(FNR_OPPFYLLER_KRAV, AktorId.of("AKTØRID"))
    }
}