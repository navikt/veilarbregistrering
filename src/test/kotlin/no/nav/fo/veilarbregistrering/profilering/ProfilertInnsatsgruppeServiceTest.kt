package no.nav.fo.veilarbregistrering.profilering

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.oppfolging.*
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ProfilertInnsatsgruppeServiceTest {
    private val oppfolgingGateway = mockk<OppfolgingGateway>()
    private val profileringRepository = mockk<ProfileringRepository>()
    private val brukerRegistreringRepository = mockk<BrukerRegistreringRepository>()
    private lateinit var profilertInnsatsgruppeService: ProfilertInnsatsgruppeService

    @BeforeEach
    fun setUp() {
        every {
            brukerRegistreringRepository
                .finnOrdinaerBrukerregistreringForAktorIdOgTilstand(
                    any(), any()
                )
        } returns listOf(OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering())

        profilertInnsatsgruppeService =
            ProfilertInnsatsgruppeService(oppfolgingGateway, profileringRepository, brukerRegistreringRepository)
    }

    @Test
    fun `hentProfilering returner en Pair av Innsatsgruppe, Servicegruppe`() {
        every { oppfolgingGateway.arenaStatus(Foedselsnummer("123")) } returns ArenaStatus(
            Servicegruppe("IVURD"),
            Rettighetsgruppe("AAP"),
            Formidlingsgruppe("ARBS")
        )
        every { profileringRepository.hentProfileringForId(0) } returns Profilering(
            Innsatsgruppe.STANDARD_INNSATS,
            42,
            true
        )

        val hentProfilering =
            profilertInnsatsgruppeService.hentProfilering(Bruker(Foedselsnummer("123"), AktorId("456")))

        assertEquals(hentProfilering, Pair(Innsatsgruppe.STANDARD_INNSATS, Servicegruppe("IVURD")))
    }

    @Test
    fun `erStandardInnsats bruker innsattsgruppe når servicegruppe er IVURD`() {
        every { oppfolgingGateway.arenaStatus(Foedselsnummer("123")) } returns ArenaStatus(
            Servicegruppe("IVURD"),
            Rettighetsgruppe("AAP"),
            Formidlingsgruppe("ARBS")
        )
        every { profileringRepository.hentProfileringForId(0) } returns Profilering(
            Innsatsgruppe.STANDARD_INNSATS,
            42,
            true
        )

        assertTrue { profilertInnsatsgruppeService.erStandardInnsats(Bruker(Foedselsnummer("123"), AktorId("456"))) }

        every { profileringRepository.hentProfileringForId(0) } returns Profilering(
            Innsatsgruppe.SITUASJONSBESTEMT_INNSATS,
            42,
            true
        )

        assertFalse { profilertInnsatsgruppeService.erStandardInnsats(Bruker(Foedselsnummer("123"), AktorId("456"))) }
    }

    @Test
    fun `erStandardInnsats bruker servicegruppe når den er vurdert`() {
        every { profileringRepository.hentProfileringForId(0) } returns Profilering(
            Innsatsgruppe.STANDARD_INNSATS,
            42,
            true
        )
        every { oppfolgingGateway.arenaStatus(Foedselsnummer("123")) } returns ArenaStatus(
            Servicegruppe("BFORM"),
            Rettighetsgruppe("AAP"),
            Formidlingsgruppe("ARBS")
        )

        assertFalse { profilertInnsatsgruppeService.erStandardInnsats(Bruker(Foedselsnummer("123"), AktorId("456"))) }

        every { oppfolgingGateway.arenaStatus(Foedselsnummer("123")) } returns ArenaStatus(
            Servicegruppe("IKVAL"),
            Rettighetsgruppe("AAP"),
            Formidlingsgruppe("ARBS")
        )

        assertTrue { profilertInnsatsgruppeService.erStandardInnsats(Bruker(Foedselsnummer("123"), AktorId("456"))) }
    }
}
