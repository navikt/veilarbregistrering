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


open class VeilederAutorisasjonService(
    private val veilarbPep: Pep,
    private val authContextHolder: AuthContextHolder,
    private val metricsService: MetricsService) : AutorisasjonService {

    override fun sjekkLesetilgangTilBruker(bruker: Foedselsnummer) = sjekkLesetilgangTilBruker(tilEksternId(bruker))
    override fun sjekkSkrivetilgangTilBruker(bruker: Foedselsnummer) = sjekkSkrivetilgangTilBruker(tilEksternId(bruker))

    private fun tilEksternId(bruker: Foedselsnummer) = Fnr(bruker.stringValue())

    private fun sjekkLesetilgangTilBruker(brukerId: EksternBrukerId) {
        validateState()
        if (!harTilgang(ActionId.READ, brukerId)) throw AutorisasjonException()
    }

    private fun sjekkSkrivetilgangTilBruker(brukerId: EksternBrukerId) {
        validateState()
        if (!harTilgang(ActionId.WRITE, brukerId)) throw AutorisasjonException()
    }

    private fun harTilgang(handling: ActionId, bruker: EksternBrukerId): Boolean {
        val navIdent = navIdentClaim()
        LOG.info("harVeilederTilgangTilPerson utfører $handling for ${UserRole.INTERN}-rolle")
        registrerAutorisationEvent(true, handling, UserRole.INTERN)
        return veilarbPep.harVeilederTilgangTilPerson(navIdent, handling, bruker)
    }

    private fun navIdentClaim(): NavIdent = authContextHolder.hentNavIdForOboTokens()

    private fun registrerAutorisationEvent(navIdentFlagg: Boolean, handling: ActionId, userRole: UserRole) {
        metricsService.registrer(
            Events.AUTORISASJON,
            Tag.of("navident", navIdentFlagg.toString().lowercase())   ,
            Tag.of("handling", handling.id),
            Tag.of("rolle", userRole.name.lowercase())
        )
    }

    override fun erVeileder(): Boolean = true

    override val innloggetVeilederIdent: String
        get() {
            validateState()
            return authContextHolder.hentNavIdForOboTokens().toString()
        }

    private fun validateState() {
        if (!authContextHolder.erInternBruker())
            throw UnsupportedOperationException("Denne strategien skal kun benyttes for ${UserRole.INTERN} - ikke ${authContextHolder.role}")
    }

    fun AuthContextHolder.hentNavIdForOboTokens(): NavIdent =
        this.requireIdTokenClaims()
            .getStringClaim(AAD_NAV_IDENT_CLAIM)
            .takeIf(IdentUtils::erGydligNavIdent)
            ?.let(NavIdent::of)
            ?:throw IllegalStateException("Fant ikke NAV-ident. Denne strategien skal kun benyttes for ${UserRole.INTERN} - ikke ${authContextHolder.role}")

    companion object {
        private val LOG = LoggerFactory.getLogger(VeilederAutorisasjonService::class.java)
    }
}