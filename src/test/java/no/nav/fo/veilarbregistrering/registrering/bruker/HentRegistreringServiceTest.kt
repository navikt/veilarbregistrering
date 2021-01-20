package no.nav.fo.veilarbregistrering.registrering.bruker

import io.mockk.every
import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashService
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr.Companion.of
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HentRegistreringServiceTest {
    private lateinit var hentRegistreringService: HentRegistreringService

    @BeforeEach
    fun setup() {
        val manuellRegistreringRepository: ManuellRegistreringRepository = mockk()
        val unleashService: UnleashService = mockk()
        val norg2Gateway: Norg2Gateway = mockk()
        every { unleashService.isEnabled(any()) } returns true
        hentRegistreringService = HentRegistreringService(null, null, manuellRegistreringRepository, norg2Gateway)
        val enheter: Map<Enhetnr, NavEnhet> = mapOf(
            of("1234") to NavEnhet("1234", "TEST1"),
            of("5678") to NavEnhet("5678", "TEST2")
        )
        every { norg2Gateway.hentAlleEnheter() } returns enheter
    }

    @Test
    fun skalFinneRiktigEnhet() {
        val enhet = hentRegistreringService.finnEnhet(of("1234"))
        Assertions.assertThat(enhet).hasValue(NavEnhet("1234", "TEST1"))
    }

    @Test
    fun skalReturnereEmptyHvisIngenEnhetErFunnet() {
        val enhet = hentRegistreringService.finnEnhet(of("2345"))
        Assertions.assertThat(enhet).isEmpty
    }
}