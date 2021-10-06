package no.nav.fo.veilarbregistrering.registrering.bruker

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BrukerTilstandServiceTest {
    private lateinit var oppfolgingGateway: OppfolgingGateway
    private lateinit var brukerRegistreringRepository: BrukerRegistreringRepository
    private lateinit var brukerTilstandService: BrukerTilstandService
    @BeforeEach
    fun setUp() {
        oppfolgingGateway = mockk()
        brukerRegistreringRepository = mockk(relaxed = true)
        brukerTilstandService = BrukerTilstandService(
            oppfolgingGateway,
            brukerRegistreringRepository
        )
    }

    @Test
    fun `brukersTilstand skal gi sykmeldtRegistrering når bruker er sykmeldt med arbeidsgiver`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            false,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe.of("IYT")
        )
        every { oppfolgingGateway.hentOppfolgingsstatus(any()) } returns oppfolgingsstatus
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(testBruker)
        Assertions.assertThat(brukersTilstand.registreringstype).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }

    companion object {
        private val testBruker = Bruker.of(Foedselsnummer.of("11019141466"), AktorId("1"), emptyList())
    }
}