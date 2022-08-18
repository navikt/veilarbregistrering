package no.nav.fo.veilarbregistrering.autorisasjon

import io.micrometer.core.instrument.Tag
import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.Fnr
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.slf4j.LoggerFactory


open class PersonbrukerAutorisasjonService(
    private val veilarbPep: Pep,
    private val authContextHolder: AuthContextHolder,
    private val metricsService: MetricsService
) : AutorisasjonService {

    override fun sjekkLesetilgangTilBruker(bruker: Foedselsnummer) = sjekkTilgang(ActionId.READ, tilEksternId(bruker))
    override fun sjekkSkrivetilgangTilBruker(bruker: Foedselsnummer) =
        sjekkTilgang(ActionId.WRITE, tilEksternId(bruker))

    private fun tilEksternId(bruker: Foedselsnummer) = Fnr(bruker.stringValue())

    private fun sjekkTilgang(handling: ActionId, bruker: EksternBrukerId) {
        if (rolle() != UserRole.EKSTERN) throw AutorisasjonValideringException("Kan ikke utføre tilgangskontroll for personbruker med rolle ${rolle()}")
        LOG.info("harTilgangTilPerson utfører $handling for ${UserRole.EKSTERN}-rolle")
        registrerAutorisationEvent(handling)

        if (!veilarbPep.harTilgangTilPerson(innloggetBrukerToken, handling, bruker)) {
            if (isDevelopment()) {
                if (innloggetMedNivå3()) throw AutorisasjonException("Bruker mangler $handling-tilgang til ekstern bruker pga level 3")
            }
            throw AutorisasjonException("Bruker mangler $handling-tilgang til ekstern bruker")
        }
    }

    private fun innloggetMedNivå3(): Boolean{
        LOG.info("Forsøker å hente innloggingsnivå")
        try {
            val innloggingsnivå = authContextHolder.hentInnloggingsnivå()
            innloggingsnivå.let {
                LOG.info("Fant innloggsnivå med nivå $it")
                if ("Level3" == it) {
                    return true
                }
            }

        } catch (e: RuntimeException) {
            LOG.error("Uthenting av innloggingsnivå feilet.", e)
        }

        return false
    }

    private fun rolle(): UserRole = authContextHolder.role.orElseThrow { IllegalStateException("Ingen role funnet") }

    private fun registrerAutorisationEvent(handling: ActionId) {
        metricsService.registrer(
            Events.AUTORISASJON,
            Tag.of("navident", "false"),
            Tag.of("handling", handling.id),
            Tag.of("rolle", UserRole.EKSTERN.name.lowercase())
        )
    }

    private fun AuthContextHolder.hentInnloggingsnivå(): String? {
        return idTokenClaims.flatMap { getStringClaim(it, "acr") }.map { it }.orElse(null)
    }

    private val innloggetBrukerToken: String
        get() = authContextHolder.idTokenString
            .orElseThrow { AutorisasjonValideringException("Fant ikke token for innlogget personbruker") }

    override val innloggetVeilederIdent: String
        get() {
            throw AutorisasjonValideringException("Prøver å hente veilederident fra tilgangskontroll for personbruker.")
        }

    override fun erVeileder(): Boolean = false

    companion object {
        private val LOG = LoggerFactory.getLogger(PersonbrukerAutorisasjonService::class.java)
    }
}