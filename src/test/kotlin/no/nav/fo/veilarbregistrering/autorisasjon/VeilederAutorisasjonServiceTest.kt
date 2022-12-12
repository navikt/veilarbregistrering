package no.nav.fo.veilarbregistrering.autorisasjon

import com.nimbusds.jwt.JWTClaimsSet
import io.micrometer.core.instrument.Tag
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.Constants.AAD_NAV_IDENT_CLAIM
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class VeilederAutorisasjonServiceTest {

    private val pep : Pep = mockk()
    private val authContextHolder : AuthContextHolder = mockk()
    private val metricsService : MetricsService = mockk()
    private val autorisasjonService = VeilederAutorisasjonService(pep, authContextHolder, metricsService)

    @Test
    fun `gitt at veileder er satt opp med lesetilgang til bruker i ABAC (pep) skal ingen exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.INTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().claim(AAD_NAV_IDENT_CLAIM, NAV_IDENT.get()).build()
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harVeilederTilgangTilPerson(NAV_IDENT, ActionId.READ, PERSON_AREMARK) } returns true

        assertDoesNotThrow { autorisasjonService.sjekkLesetilgangTilBruker(aremark()) }
    }

    @Test
    fun `gitt at veileder IKKE er satt opp med lesetilgang til bruker i ABAC (pep) skal det kastes en exception`() {
        every { authContextHolder.role} returns Optional.of(UserRole.INTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().claim(AAD_NAV_IDENT_CLAIM, NAV_IDENT.get()).build()
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harVeilederTilgangTilPerson(NAV_IDENT, ActionId.READ, PERSON_AREMARK) } returns false

        val exception = assertThrows(AutorisasjonException::class.java) {
            autorisasjonService.sjekkLesetilgangTilBruker(aremark())
        }

        assertEquals("Veileder mangler READ-tilgang til ekstern bruker", exception.message)
    }

    @Test
    fun `gitt at veileder er satt opp med skrivetilgang til bruker i ABAC (pep) skal ingen exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.INTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().claim(AAD_NAV_IDENT_CLAIM, NAV_IDENT.get()).build()
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harVeilederTilgangTilPerson(NAV_IDENT, ActionId.WRITE, PERSON_AREMARK) } returns true

        assertDoesNotThrow { autorisasjonService.sjekkSkrivetilgangTilBruker(aremark()) }
    }

    @Test
    fun `gitt at veileder IKKE er satt opp med skrivetilgang til bruker i ABAC (pep) skal det kastes en exception`() {
        every { authContextHolder.role} returns Optional.of(UserRole.INTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().claim(AAD_NAV_IDENT_CLAIM, NAV_IDENT.get()).build()
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harVeilederTilgangTilPerson(NAV_IDENT, ActionId.WRITE, PERSON_AREMARK) } returns false

        val exception = assertThrows(AutorisasjonException::class.java) {
            autorisasjonService.sjekkSkrivetilgangTilBruker(aremark())
        }

        assertEquals("Veileder mangler WRITE-tilgang til ekstern bruker", exception.message)
    }

    @Test
    fun `gitt at tilgangskontroll for veileder brukes av personbruker ved skriv skal exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.EKSTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harTilgangTilPerson("innloggetBrukerIdToken", ActionId.WRITE,
            PersonbrukerAutorisasjonServiceTest.PERSON_AREMARK
        ) } returns false

        val exception = assertThrows(AutorisasjonValideringException::class.java) {
            autorisasjonService.sjekkSkrivetilgangTilBruker(aremark())
        }

        assertEquals("Kan ikke utføre WRITE for veileder med rolle EKSTERN", exception.message)
    }

    @Test
    fun `gitt at tilgangskontroll for veileder brukes av personbruker ved les skal exception kastes`() {
        every { authContextHolder.role} returns Optional.of(UserRole.EKSTERN)
        every { authContextHolder.requireIdTokenClaims() } returns JWTClaimsSet.Builder().build()
        every { authContextHolder.idTokenString } returns Optional.of("innloggetBrukerIdToken")
        every { metricsService.registrer(any(), *anyVararg<Tag>()) } just Runs
        every { pep.harTilgangTilPerson("innloggetBrukerIdToken", ActionId.READ,
            PersonbrukerAutorisasjonServiceTest.PERSON_AREMARK
        ) } returns false

        val exception = assertThrows(AutorisasjonValideringException::class.java) {
            autorisasjonService.sjekkLesetilgangTilBruker(aremark())
        }

        assertEquals("Kan ikke utføre READ for veileder med rolle EKSTERN", exception.message)
    }

    companion object {
        val NAV_IDENT : NavIdent = NavIdent.of("H123456")
        val PERSON_AREMARK : Fnr = Fnr.of(aremark().stringValue())
    }
}