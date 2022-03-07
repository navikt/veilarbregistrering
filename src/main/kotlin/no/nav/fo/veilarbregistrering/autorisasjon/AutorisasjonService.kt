package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.Constants.AAD_NAV_IDENT_CLAIM
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.auth.utils.IdentUtils
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


open class AutorisasjonService(private val veilarbPep: Pep, private val authContextHolder: AuthContextHolder) {

    fun sjekkLesetilgangTilBruker(bruker: Foedselsnummer) = sjekkLesetilgangTilBruker(tilEksternId(bruker))
    fun sjekkLesetilgangTilBruker(bruker: AktorId) = sjekkLesetilgangTilBruker(tilEksternId(bruker))
    fun sjekkSkrivetilgangTilBruker(bruker: Foedselsnummer) = sjekkSkrivetilgangTilBruker(tilEksternId(bruker))
    fun sjekkSkrivetilgangTilBruker(bruker: AktorId) = sjekkSkrivetilgangTilBruker(tilEksternId(bruker))

    private fun tilEksternId(bruker: Foedselsnummer) = Fnr(bruker.stringValue())
    private fun tilEksternId(bruker: AktorId) = no.nav.common.types.identer.AktorId(bruker.aktorId)

    private fun sjekkLesetilgangTilBruker(brukerId: EksternBrukerId) {
        if (rolle() == UserRole.SYSTEM) return
        if (!harTilgang(ActionId.READ, brukerId)) throw AutorisasjonException()
    }

    private fun rolle(): UserRole = authContextHolder.role.orElseThrow { IllegalStateException("Ingen role funnet") }

    private fun sjekkSkrivetilgangTilBruker(brukerId: EksternBrukerId) {
        if (!harTilgang(ActionId.WRITE, brukerId)) throw AutorisasjonException()
    }

    private fun navIdentClaim(): NavIdent? = authContextHolder.hentNavIdForOboTokens()

    private fun harTilgang(handling: ActionId, bruker: EksternBrukerId): Boolean {
        val navIdent = navIdentClaim()
        return if (navIdent != null) {
            veilarbPep.harVeilederTilgangTilPerson(navIdent, handling, bruker)
        } else
        {
            veilarbPep.harTilgangTilPerson(innloggetBrukerToken, handling, bruker)
        }
    }

    private val innloggetBrukerToken: String
        get() = authContextHolder.idTokenString
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Fant ikke token for innlogget bruker") }

    // NAV ident, fnr eller annen ID
    private val innloggetBrukerIdent: String
        get() = authContextHolder.subject
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "NAV ident is missing") }

    val innloggetVeilederIdent: String
        get() {
            if (!erInternBruker()) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            }
            return (authContextHolder.hentNavIdForOboTokens()?.toString() ?: innloggetBrukerIdent)
        }

    fun AuthContextHolder.hentNavIdForOboTokens(): NavIdent? =
        this.requireIdTokenClaims()
            .getStringClaim(AAD_NAV_IDENT_CLAIM)
            .takeIf(IdentUtils::erGydligNavIdent)
            ?.let(NavIdent::of)

    fun erVeileder(): Boolean = erInternBruker()
    fun erInternBruker(): Boolean = authContextHolder.erInternBruker()
}