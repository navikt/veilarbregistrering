package no.nav.fo.veilarbregistrering.autorisasjon

import io.micrometer.core.instrument.Tag
import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.Constants.AAD_NAV_IDENT_CLAIM
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.auth.utils.IdentUtils
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


open class AutorisasjonService(
    private val veilarbPep: Pep,
    private val authContextHolder: AuthContextHolder,
    private val metricsService: MetricsService) {

    fun sjekkLesetilgangTilBruker(bruker: Foedselsnummer) = sjekkLesetilgangTilBruker(tilEksternId(bruker))
    fun sjekkSkrivetilgangTilBruker(bruker: Foedselsnummer) = sjekkSkrivetilgangTilBruker(tilEksternId(bruker))

    private fun tilEksternId(bruker: Foedselsnummer) = Fnr(bruker.stringValue())

    private fun sjekkLesetilgangTilBruker(brukerId: EksternBrukerId) {
        if (rolle() == UserRole.SYSTEM) return
        if (!harTilgang(ActionId.READ, brukerId)) throw AutorisasjonException()
    }

    private fun rolle(): UserRole = authContextHolder.role.orElseThrow { IllegalStateException("Ingen role funnet") }

    private fun sjekkSkrivetilgangTilBruker(brukerId: EksternBrukerId) {
        if (!harTilgang(ActionId.WRITE, brukerId)) throw AutorisasjonException()
    }

    private fun harTilgang(handling: ActionId, bruker: EksternBrukerId): Boolean {
        val navIdent = navIdentClaim()
        return if (navIdent != null) {
            LOG.info("harVeilederTilgangTilPerson utfører $handling for ${rolle()}-rolle")
            registrerAutorisationEvent(true, handling, rolle())
            veilarbPep.harVeilederTilgangTilPerson(navIdent, handling, bruker)
        } else {
            LOG.info("harTilgangTilPerson utfører $handling for ${rolle()}-rolle")
            registrerAutorisationEvent(false, handling, rolle())
            veilarbPep.harTilgangTilPerson(innloggetBrukerToken, handling, bruker)
        }
    }

    private fun registrerAutorisationEvent(navIdentFlagg: Boolean, handling: ActionId, userRole: UserRole) {
        metricsService.registrer(
            Events.AUTORISASJON,
            Tag.of("navident", navIdentFlagg.toString().lowercase())   ,
            Tag.of("handling", handling.id),
            Tag.of("rolle", userRole.name.lowercase())
        )
    }

    private fun navIdentClaim(): NavIdent? = authContextHolder.hentNavIdForOboTokens()

    private val innloggetBrukerToken: String
        get() = authContextHolder.idTokenString
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Fant ikke token for innlogget bruker") }

    val innloggetVeilederIdent: String
        get() {
            if (!erInternBruker()) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            }
            return (authContextHolder.hentNavIdForOboTokens()?.toString() ?: innloggetBrukerIdent)
        }

    // NAV ident, fnr eller annen ID
    private val innloggetBrukerIdent: String
        get() = authContextHolder.subject
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "NAV ident is missing") }

    fun AuthContextHolder.hentNavIdForOboTokens(): NavIdent? =
        this.requireIdTokenClaims()
            .getStringClaim(AAD_NAV_IDENT_CLAIM)
            .takeIf(IdentUtils::erGydligNavIdent)
            ?.let(NavIdent::of)

    fun erVeileder(): Boolean = erInternBruker()
    fun erInternBruker(): Boolean = authContextHolder.erInternBruker()

    companion object {
        private val LOG = LoggerFactory.getLogger(AutorisasjonService::class.java)
    }
}