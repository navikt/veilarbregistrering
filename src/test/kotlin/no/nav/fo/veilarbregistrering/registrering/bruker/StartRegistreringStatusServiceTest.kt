package no.nav.fo.veilarbregistrering.registrering.bruker

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.adapter.ErUnderOppfolgingDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.ArenaStatusDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.KanReaktiveresDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.VeilarbarenaClient
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class StartRegistreringStatusServiceTest {
    private lateinit var brukerRegistreringService: StartRegistreringStatusService
    private lateinit var arbeidsforholdGateway: ArbeidsforholdGateway
    private lateinit var oppfolgingClient: OppfolgingClient
    private lateinit var veilarbarenaClient: VeilarbarenaClient
    private lateinit var pdlOppslagGateway: PdlOppslagGateway

    private val CONSUMER_ID_TEST = "TEST"
    @BeforeEach
    fun setup() {
        arbeidsforholdGateway = mockk()
        oppfolgingClient = mockk()
        veilarbarenaClient = mockk()
        pdlOppslagGateway = mockk()
        val metricsService: MetricsService = mockk(relaxed = true)
        val oppfolgingGateway = OppfolgingGatewayImpl(oppfolgingClient, veilarbarenaClient)
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
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns GeografiskTilknytning("030109")

        val startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(BRUKER_INTERN, CONSUMER_ID_TEST)

        Assertions.assertThat(startRegistreringStatus.registreringType == RegistreringType.ALLEREDE_REGISTRERT).isTrue
    }

    @Test
    fun skalReturnereAtBrukerOppfyllerBetingelseOmArbeidserfaring() {
        mockInaktivBrukerUtenReaktivering()
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring()
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns GeografiskTilknytning("030109")

        val startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(BRUKER_INTERN, CONSUMER_ID_TEST)

        Assertions.assertThat(startRegistreringStatus.jobbetSeksAvTolvSisteManeder).isTrue
    }

    @Test
    fun skalReturnereFalseOmIkkeUnderOppfolging() {
        mockOppfolgingMedRespons()
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav())
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns GeografiskTilknytning("030109")

        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)

        Assertions.assertThat(startRegistreringStatus.registreringType == RegistreringType.ALLEREDE_REGISTRERT).isFalse
    }

    @Test
    fun skalReturnereAlleredeUnderOppfolging() {
        mockArbeidssokerSomHarAktivOppfolging()
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns GeografiskTilknytning("030109")

        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)

        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns GeografiskTilknytning("030109")
        Assertions.assertThat(startRegistreringStatus.registreringType == RegistreringType.ALLEREDE_REGISTRERT).isTrue
    }

    @Test
    fun skalReturnereReaktivering() {
        mockOppfolgingMedRespons()
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav())
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns GeografiskTilknytning("030109")

        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)

        Assertions.assertThat(startRegistreringStatus.registreringType).isEqualTo(RegistreringType.REAKTIVERING)
    }

    @Test
    fun skalReturnereSykmeldtRegistrering() {
        mockSykmeldtBruker()
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns GeografiskTilknytning("030109")

        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)

        Assertions.assertThat(startRegistreringStatus.registreringType)
            .isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }

    @Test
    fun gitt_at_geografiskTilknytning_ikke_ble_funnet_skal_null_returneres() {
        mockInaktivBrukerUtenReaktivering()
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring()
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns null

        val startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN)

        Assertions.assertThat(startRegistreringStatus).isNotNull
        Assertions.assertThat(startRegistreringStatus.geografiskTilknytning).isNull()
    }

    @Test
    fun gitt_at_geografiskTilknytning_er_1234_skal_1234_returneres() {
        mockInaktivBrukerUtenReaktivering()
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring()
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns GeografiskTilknytning("1234")

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
        return brukerRegistreringService.hentStartRegistreringStatus(bruker, CONSUMER_ID_TEST)
    }

    private fun arbeidsforholdSomOppfyllerKrav(): List<Arbeidsforhold> {
        return listOf(
            Arbeidsforhold(
                "orgnummer", "styrk", LocalDate.of(2017, 1, 10), null, null
            )
        )
    }

    private fun mockArbeidssokerSomHarAktivOppfolging() {
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(true)
        every { veilarbarenaClient.kanReaktiveres(any()) } returns KanReaktiveresDto(false)
        every { veilarbarenaClient.arenaStatus(any()) } returns ArenaStatusDto(formidlingsgruppe = "ARBS", kvalifiseringsgruppe = "IKVAL", rettighetsgruppe = "IYT")
    }



    private fun mockSykmeldtBruker() {
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(false)
        every { veilarbarenaClient.arenaStatus(any()) } returns ArenaStatusDto(formidlingsgruppe = "IARBS", kvalifiseringsgruppe = "VURDI", rettighetsgruppe = "IYT")
        every { veilarbarenaClient.kanReaktiveres(any()) } returns KanReaktiveresDto(false)
    }

    private fun mockIkkeSykmeldtBruker() {
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(false)
        every { veilarbarenaClient.kanReaktiveres(any()) } returns KanReaktiveresDto(false)
        every { veilarbarenaClient.arenaStatus(any()) } returns ArenaStatusDto(formidlingsgruppe = "ARBS", kvalifiseringsgruppe = "IKVAL", rettighetsgruppe = "IYT")
    }

    private fun mockArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>) =
        every { arbeidsforholdGateway.hentArbeidsforhold(any()) } returns FlereArbeidsforhold(arbeidsforhold)

    private fun mockInaktivBrukerUtenReaktivering() {
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(false)
        every { veilarbarenaClient.kanReaktiveres(any()) } returns KanReaktiveresDto(false)
        every { veilarbarenaClient.arenaStatus(any()) } returns ArenaStatusDto(formidlingsgruppe = "ISERV", kvalifiseringsgruppe = "IVURD", rettighetsgruppe = "IYT")
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

    private fun mockOppfolgingMedRespons() {
        every { pdlOppslagGateway.hentGeografiskTilknytning(any()) } returns GeografiskTilknytning("030109")
        every { oppfolgingClient.erBrukerUnderOppfolging(any()) } returns ErUnderOppfolgingDto(true)
        every { veilarbarenaClient.kanReaktiveres(any()) } returns KanReaktiveresDto(true)
        every { veilarbarenaClient.arenaStatus(any()) } returns ArenaStatusDto(formidlingsgruppe = "ARBS", kvalifiseringsgruppe = "IKVAL", rettighetsgruppe = "IYT")
    }

    companion object {
        private val FNR_OPPFYLLER_KRAV =
            FoedselsnummerTestdataBuilder.fodselsnummerOnDateMinusYears(LocalDate.now(), 40)
        private val BRUKER_INTERN = Bruker(FNR_OPPFYLLER_KRAV, AktorId("AKTØRID"))
    }
}