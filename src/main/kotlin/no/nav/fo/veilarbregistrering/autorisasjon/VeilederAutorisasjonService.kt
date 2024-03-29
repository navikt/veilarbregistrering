package no.nav.fo.veilarbregistrering.autorisasjon

import io.micrometer.core.instrument.Tag
import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.Constants.AAD_NAV_IDENT_CLAIM
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.auth.utils.IdentUtils
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.slf4j.LoggerFactory


open class VeilederAutorisasjonService(
    private val veilarbPep: Pep,
    private val authContextHolder: AuthContextHolder,
    private val metricsService: MetricsService
) : AutorisasjonService {

    override fun sjekkLesetilgangTilBrukerMedNivå3(bruker: Bruker, kontekst: String) {
        throw AutorisasjonValideringException("Kan ikke utføre ${ActionId.READ} på nivå 3 for veileder")
    }
    override fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer) = sjekkTilgang(ActionId.READ, fnr)
    override fun sjekkLesetilgangTilBruker(bruker: Bruker, kontekst: String) {
        throw AutorisasjonValideringException("Tilgangskontroll uten ABAC for veileder er ikke støttet")
    }

    override fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer) = sjekkTilgang(ActionId.WRITE, fnr)
    override fun sjekkSkrivetilgangTilBruker(bruker: Bruker, kontekst: String) {
        throw AutorisasjonValideringException("Tilgangskontroll uten ABAC for veileder er ikke støttet")
    }

    private fun sjekkTilgang(handling: ActionId, foedselsnummer: Foedselsnummer) {
        if (rolle() != UserRole.INTERN) throw AutorisasjonValideringException("Kan ikke utføre $handling for veileder med rolle ${rolle()}")

        val navIdent = navIdentClaim()
            ?: throw AutorisasjonValideringException("Fant ikke NAV-ident fra claim i tilgangskontroll for veileder.")

        LOG.info("harVeilederTilgangTilPerson utfører $handling for ${UserRole.INTERN}-rolle")
        registrerAutorisationEvent(handling)
        if (!veilarbPep.harVeilederTilgangTilPerson(navIdent, handling, tilEksternId(foedselsnummer)))
            throw AutorisasjonException("Veileder mangler $handling-tilgang til ekstern bruker")
    }

    private fun rolle(): UserRole = authContextHolder.role.orElseThrow { IllegalStateException("Ingen role funnet") }

    private fun registrerAutorisationEvent(handling: ActionId) {
        metricsService.registrer(
            Events.AUTORISASJON,
            Tag.of("navident", "true"),
            Tag.of("handling", handling.id),
            Tag.of("rolle", UserRole.INTERN.name.lowercase())
        )
    }

    private fun tilEksternId(bruker: Foedselsnummer) = Fnr(bruker.stringValue())

    override fun sjekkSkrivetilgangTilBrukerForSystembruker(fnr: Foedselsnummer, cefMelding: CefMelding) {
        throw AutorisasjonValideringException("Veileder kan ikke utføre ${ActionId.WRITE} for systembruker")
    }

    private fun navIdentClaim(): NavIdent? = authContextHolder.hentNavIdForOboTokens()

    override val innloggetVeilederIdent: String
        get() {
            return (authContextHolder.hentNavIdForOboTokens()?.toString() ?: innloggetBrukerIdent)
        }

    // NAV ident, fnr eller annen ID
    private val innloggetBrukerIdent: String
        get() = authContextHolder.subject
            .orElseThrow { AutorisasjonValideringException("Fant ikke NAV-ident i subject-claim i token i tilgangskontroll for veileder") }

    private fun AuthContextHolder.hentNavIdForOboTokens(): NavIdent? =
        this.requireIdTokenClaims()
            .getStringClaim(AAD_NAV_IDENT_CLAIM)
            .takeIf(IdentUtils::erGydligNavIdent)
            ?.let(NavIdent::of)

    override fun erVeileder(): Boolean = true

    companion object {
        private val LOG = LoggerFactory.getLogger(VeilederAutorisasjonService::class.java)
    }
}