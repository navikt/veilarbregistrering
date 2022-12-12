package no.nav.fo.veilarbregistrering.autorisasjon

import com.nimbusds.jwt.JWTClaimsSet
import io.micrometer.core.instrument.Tag
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class SystembrukerAutorisasjonServiceTest {

    private val authContextHolder : AuthContextHolder = mockk()
    private val metricsService : MetricsService = mockk()
    private val autorisasjonService = SystembrukerAutorisasjonService(authContextHolder, metricsService)

    @Test
    fun `systembruker skal alltid ha lesetilgang`() {
        every { authContextHolder.role} returns Optional.of(UserRole.SYSTEM)
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs

        assertDoesNotThrow { autorisasjonService.sjekkLesetilgangTilBruker(aremark()) }
    }

    @Test
    fun `systembruker skal aldri ha skrivetilgang`() {
        every { authContextHolder.role} returns Optional.of(UserRole.SYSTEM)
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs

        val exception = assertThrows(AutorisasjonValideringException::class.java) {
            autorisasjonService.sjekkSkrivetilgangTilBruker(aremark())
        }

        assertEquals("Systembruker har ikke skrivetilgang til bruker", exception.message)
    }

    @Test
    fun `gitt at tilgangskontroll for systembruker brukes av veileder ved les skal exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.INTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs

        val exception = assertThrows(AutorisasjonValideringException::class.java) {
            autorisasjonService.sjekkLesetilgangTilBruker(aremark())
        }

        assertEquals("Kan ikke utføre READ for systembruker med rolle INTERN", exception.message)
    }

    @Test
    fun `gitt at tilgangskontroll for systembruker brukes av personbruker ved les skal exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.EKSTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs

        val exception = assertThrows(AutorisasjonValideringException::class.java) {
            autorisasjonService.sjekkLesetilgangTilBruker(aremark())
        }

        assertEquals("Kan ikke utføre READ for systembruker med rolle EKSTERN", exception.message)
    }
}