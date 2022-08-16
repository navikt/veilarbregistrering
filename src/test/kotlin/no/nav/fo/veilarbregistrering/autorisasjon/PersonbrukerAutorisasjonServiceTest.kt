package no.nav.fo.veilarbregistrering.autorisasjon

import com.nimbusds.jwt.JWTClaimsSet
import io.micrometer.core.instrument.Tag
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.Fnr
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class PersonbrukerAutorisasjonServiceTest {

    private val pep : Pep = mockk()
    private val authContextHolder : AuthContextHolder = mockk()
    private val metricsService : MetricsService = mockk()
    private val autorisasjonService = PersonbrukerAutorisasjonService(pep, authContextHolder, metricsService)

    @Test
    fun `gitt at personbruker har lesetilgang til seg selv skal ingen exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.EKSTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harTilgangTilPerson("innloggetBrukerIdToken", ActionId.READ, PERSON_AREMARK) } returns true

        assertDoesNotThrow { autorisasjonService.sjekkLesetilgangTilBruker(aremark()) }
    }

    @Test
    fun `gitt at personbruker IKKE har lesetilgang til seg selv (?) skal exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.EKSTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harTilgangTilPerson("innloggetBrukerIdToken", ActionId.READ, PERSON_AREMARK) } returns false

        val exception = assertThrows(AutorisasjonException::class.java) {
            autorisasjonService.sjekkLesetilgangTilBruker(aremark())
        }

        assertEquals("Bruker mangler READ-tilgang til ekstern bruker", exception.message)
    }

    @Test
    fun `gitt at personbruker har skrivetilgang til seg selv skal ingen exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.EKSTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harTilgangTilPerson("innloggetBrukerIdToken", ActionId.WRITE, PERSON_AREMARK) } returns true

        assertDoesNotThrow { autorisasjonService.sjekkSkrivetilgangTilBruker(aremark()) }
    }

    @Test
    fun `gitt at personbruker IKKE har skrivetilgang til seg selv (?) skal exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.EKSTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harTilgangTilPerson("innloggetBrukerIdToken", ActionId.WRITE, PERSON_AREMARK) } returns false

        val exception = assertThrows(AutorisasjonException::class.java) {
            autorisasjonService.sjekkSkrivetilgangTilBruker(aremark())
        }

        assertEquals("Bruker mangler WRITE-tilgang til ekstern bruker", exception.message)
    }

    @Test
    fun `gitt at tilgangskontroll for personbruker brukes av veileder ved skriv skal exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.INTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harTilgangTilPerson("innloggetBrukerIdToken", ActionId.WRITE, PERSON_AREMARK) } returns false

        val exception = assertThrows(AutorisasjonValideringException::class.java) {
            autorisasjonService.sjekkSkrivetilgangTilBruker(aremark())
        }

        assertEquals("Kan ikke utføre tilgangskontroll for personbruker med rolle INTERN", exception.message)
    }

    @Test
    fun `gitt at tilgangskontroll for personbruker brukes av veileder ved les skal exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.INTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harTilgangTilPerson("innloggetBrukerIdToken", ActionId.READ, PERSON_AREMARK) } returns false

        val exception = assertThrows(AutorisasjonValideringException::class.java) {
            autorisasjonService.sjekkLesetilgangTilBruker(aremark())
        }

        assertEquals("Kan ikke utføre tilgangskontroll for personbruker med rolle INTERN", exception.message)
    }

    companion object {
        val PERSON_AREMARK : Fnr = Fnr.of(aremark().stringValue())
    }
}