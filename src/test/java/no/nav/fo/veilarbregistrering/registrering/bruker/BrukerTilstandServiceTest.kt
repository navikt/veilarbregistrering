package no.nav.fo.veilarbregistrering.registrering.bruker

import io.mockk.*
import no.nav.common.featuretoggle.UnleashService
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.time.LocalDate

class BrukerTilstandServiceTest {
    private lateinit var oppfolgingGateway: OppfolgingGateway
    private lateinit var sykemeldingService: SykemeldingService
    private lateinit var unleashService: UnleashService
    private lateinit var brukerRegistreringRepository: BrukerRegistreringRepository
    private lateinit var brukerTilstandService: BrukerTilstandService
    @BeforeEach
    fun setUp() {
        oppfolgingGateway = mockk()
        sykemeldingService = mockk()
        unleashService = mockk()
        brukerRegistreringRepository = mockk(relaxed = true)
        brukerTilstandService = BrukerTilstandService(
            oppfolgingGateway,
            sykemeldingService,
            unleashService,
            brukerRegistreringRepository
        )
    }

    @Test
    fun `brukers tilstand skal gi sperret når toggle ikke er skrudd på`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            false,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe.of("IYT")
        )
        every { oppfolgingGateway.hentOppfolgingsstatus(any()) } returns oppfolgingsstatus
        val sykeforlop = SykmeldtInfoData(null, false)
        every { sykemeldingService.hentSykmeldtInfoData(any()) } returns sykeforlop
        every { unleashService.isEnabled(any()) } returns false
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(testBruker)
        Assertions.assertThat(brukersTilstand.registreringstype).isEqualTo(RegistreringType.SPERRET)
    }

    @Test
    fun `brukersTilstand skal gi sykmeldtRegistrering når toggle er enablet`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            false,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe.of("IYT")
        )
        every { oppfolgingGateway.hentOppfolgingsstatus(any()) } returns oppfolgingsstatus
        val sykeforlop = SykmeldtInfoData(null, false)
        every { sykemeldingService.hentSykmeldtInfoData(any()) } returns sykeforlop
        every { unleashService.isEnabled(any()) } returns true
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(testBruker)
        Assertions.assertThat(brukersTilstand.registreringstype).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }

    @Test
    fun brukersTilstand_hvor_med_og_uten_maksdato_gir_ulike_svar() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            false,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe.of("IYT")
        )
        every { oppfolgingGateway.hentOppfolgingsstatus(any()) } returns oppfolgingsstatus
        val sykeforlop = SykmeldtInfoData(null, false)
        every { sykemeldingService.hentSykmeldtInfoData(any()) } returns sykeforlop
        every { unleashService.isEnabled(any()) } returns true
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(testBruker, true)
        Assertions.assertThat(brukersTilstand.registreringstype).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }

    @Test
    fun brukersTilstand_hvor_med_og_uten_maksdato_gir_like_svar() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            false,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe.of("IYT")
        )
        every { oppfolgingGateway.hentOppfolgingsstatus(any()) } returns oppfolgingsstatus
        val sykeforlop = SykmeldtInfoData(LocalDate.now().minusWeeks(10).toString(), true)
        every { sykemeldingService.hentSykmeldtInfoData(any()) } returns sykeforlop
        every { unleashService.isEnabled(any()) } returns true
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(testBruker, true)
        Assertions.assertThat(brukersTilstand.registreringstype).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }

    companion object {
        private val testBruker = Bruker.of(Foedselsnummer.of("11019141466"), AktorId.of("1"), emptyList())
    }
}