package no.nav.fo.veilarbregistrering.registrering.bruker

import io.mockk.every
import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr.Companion.of
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class HentRegistreringServiceTest {
    private lateinit var hentRegistreringService: HentRegistreringService
    private lateinit var brukerRegistreringRepository: BrukerRegistreringRepository

    @BeforeEach
    fun setup() {
        val manuellRegistreringRepository: ManuellRegistreringRepository = mockk()
        val unleashClient: UnleashClient = mockk()
        val norg2Gateway: Norg2Gateway = mockk()
        val profileringRepository: ProfileringRepository = mockk()
        brukerRegistreringRepository  = mockk()

        every { unleashClient.isEnabled(any()) } returns true
        every { profileringRepository.hentProfileringForId(any()) } returns profilering
        every { norg2Gateway.hentAlleEnheter() } returns enheter
        every { manuellRegistreringRepository.hentManuellRegistrering(any(), any()) } returns null

        hentRegistreringService = HentRegistreringService(brukerRegistreringRepository, null, profileringRepository, manuellRegistreringRepository, norg2Gateway)
    }

    @Test
    fun skalFinneRiktigEnhet() {
        val enhet = hentRegistreringService.finnEnhet(of("1234"))
        assertThat(enhet).hasValue(NavEnhet("1234", "TEST1"))
    }

    @Test
    fun skalReturnereEmptyHvisIngenEnhetErFunnet() {
        val enhet = hentRegistreringService.finnEnhet(of("2345"))
        assertThat(enhet).isEmpty
    }

    @Test
    fun `returnerer tom registrering hvis igangsatt registrering er for gammel`() {
        every { brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(aktorId, any()) } returns listOf(GAMMEL_BRUKERREGISTRERING)
        val igangsattOrdinaerBrukerRegistrering =
            hentRegistreringService.hentIgangsattOrdinaerBrukerRegistrering(bruker)

        assertThat(igangsattOrdinaerBrukerRegistrering).isNull()
    }

    @Test
    fun `returnerer registrering hvis igangsatt registrering ikke er for gammel`() {
        every { brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(aktorId, any()) } returns listOf(OK_IGANGSATT_REGISTRERING)
        val igangsattOrdinaerBrukerRegistrering =
            hentRegistreringService.hentIgangsattOrdinaerBrukerRegistrering(bruker)

        assertThat(igangsattOrdinaerBrukerRegistrering).isNotNull
        assertThat(igangsattOrdinaerBrukerRegistrering.id).isEqualTo(OK_IGANGSATT_REGISTRERING.id)
    }

    companion object {
        private val fnr = Foedselsnummer.of("11017724129")
        private val aktorId = AktorId("12311")
        private val bruker = Bruker.of(fnr, aktorId )
        private val gammelDato = LocalDateTime.of(2020,1,11,15,50, 20)
        private val igaar = LocalDateTime.now().minusDays(1)
        private val GAMMEL_BRUKERREGISTRERING =
            OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
                opprettetDato = gammelDato)

        private val OK_IGANGSATT_REGISTRERING = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
            opprettetDato = igaar
        )

        private val profilering = ProfileringTestdataBuilder.lagProfilering()

        val enheter: Map<Enhetnr, NavEnhet> = mapOf(
            of("1234") to NavEnhet("1234", "TEST1"),
            of("5678") to NavEnhet("5678", "TEST2")
        )
    }
}