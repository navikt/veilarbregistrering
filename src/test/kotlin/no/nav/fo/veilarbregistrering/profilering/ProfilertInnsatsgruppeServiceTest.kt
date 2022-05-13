package no.nav.fo.veilarbregistrering.profilering

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ProfilertInnsatsgruppeServiceTest {
    private val oppfolgingGateway = mockk<OppfolgingGateway>()
    private val profileringRepository = mockk<ProfileringRepository>()
    private val brukerRegistreringRepository = mockk<BrukerRegistreringRepository>()
    private lateinit var profilertInnsatsgruppeService: ProfilertInnsatsgruppeService

    @BeforeEach
    fun setUp() {
        profilertInnsatsgruppeService =
            ProfilertInnsatsgruppeService(oppfolgingGateway, profileringRepository, brukerRegistreringRepository)
    }

    @Test
    fun `returner en Pair av Innsatsgruppe, Servicegruppe`() {
        every { oppfolgingGateway.hentOppfolgingsstatus(Foedselsnummer("123")) } returns Oppfolgingsstatus(
            false,
            null,
            null,
            null,
            Servicegruppe("IVURD")
        )
        every {
            brukerRegistreringRepository
                .finnOrdinaerBrukerregistreringForAktorIdOgTilstand(
                    AktorId("456"), listOf(
                        Status.OVERFORT_ARENA,
                        Status.PUBLISERT_KAFKA,
                        Status.OPPRINNELIG_OPPRETTET_UTEN_TILSTAND
                    )
                )
        } returns listOf(OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering())

        every { profileringRepository.hentProfileringForId(0) } returns Profilering(Innsatsgruppe.STANDARD_INNSATS, 42, true)

        val hentProfilering =
            profilertInnsatsgruppeService.hentProfilering(Bruker(Foedselsnummer("123"), AktorId("456")))

        assertEquals(hentProfilering, Pair(Innsatsgruppe.STANDARD_INNSATS, Servicegruppe("IVURD")))
    }
}
