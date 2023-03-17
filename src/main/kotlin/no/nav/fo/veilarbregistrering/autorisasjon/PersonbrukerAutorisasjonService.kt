package no.nav.fo.veilarbregistrering.autorisasjon

import io.micrometer.core.instrument.Tag
import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.Constants.ID_PORTEN_PID_CLAIM
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.Fnr
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.autitLogger
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService

open class PersonbrukerAutorisasjonService(
    private val veilarbPep: Pep,
    private val authContextHolder: AuthContextHolder,
    private val metricsService: MetricsService
) : AutorisasjonService {
    override fun sjekkLesetilgangTilBrukerMedNivå3(bruker: Bruker, cefMelding: CefMelding) {
        validerTilgangOgAuditlog(cefMelding, bruker)
        validerInnloggingsnivå(listOf(INNLOGGINGSNIVÅ_3, INNLOGGINGSNIVÅ_4))
    }
    override fun sjekkLesetilgangTilBruker(bruker: Bruker, cefMelding: CefMelding) {
        validerTilgangOgAuditlog(cefMelding, bruker)
        validerInnloggingsnivå(listOf(INNLOGGINGSNIVÅ_4))
    }

    override fun sjekkSkrivetilgangTilBruker(bruker: Bruker, cefMelding: CefMelding) {
        validerTilgangOgAuditlog(cefMelding, bruker)
        validerInnloggingsnivå(listOf(INNLOGGINGSNIVÅ_4))
    }

    private fun validerTilgangOgAuditlog(
        cefMelding: CefMelding,
        bruker: Bruker
    ) {
        if (rolle() != UserRole.EKSTERN) throw AutorisasjonValideringException("Kan ikke utføre tilgangskontroll for personbruker med rolle ${rolle()}")
        logger.info("Sjekker tilgang for ${UserRole.EKSTERN}-rolle")
        autitLogger.info(cefMelding.cefMessage())
        val foedselsnummerFraToken = authContextHolder.hentFoedselsnummer()
        if (!bruker.alleFoedselsnummer().contains(foedselsnummerFraToken)) {
            throw AutorisasjonException("Personbruker ber om tilgang til noen andre enn seg selv.")
        }
    }

    private fun validerInnloggingsnivå(gyldigeInnloggingsnivåer: List<String>) {
        val innloggingsnivå = authContextHolder.hentInnloggingsnivå()
        if (gyldigeInnloggingsnivåer.contains(innloggingsnivå)) return

        throw AutorisasjonException("Personbruker ber om lesetilgang med for lavt innloggingsnivå. Bruker har $innloggingsnivå - vi krever $INNLOGGINGSNIVÅ_3 eller $INNLOGGINGSNIVÅ_4")
    }

    override fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer) = sjekkTilgang(ActionId.READ, tilEksternId(fnr))

    override fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer) =
        sjekkTilgang(ActionId.WRITE, tilEksternId(fnr))

    override fun sjekkSkrivetilgangTilBrukerForSystembruker(fnr: Foedselsnummer, cefMelding: CefMelding) {
        throw AutorisasjonException("Personbruker kan ikke utføre tilgangskontroll for systembruker")
    }

    private fun tilEksternId(bruker: Foedselsnummer) = Fnr(bruker.stringValue())

    private fun sjekkTilgang(handling: ActionId, bruker: EksternBrukerId) {
        if (rolle() != UserRole.EKSTERN) throw AutorisasjonValideringException("Kan ikke utføre tilgangskontroll for personbruker med rolle ${rolle()}")
        logger.info("harTilgangTilPerson utfører $handling for ${UserRole.EKSTERN}-rolle")

        if (!veilarbPep.harTilgangTilPerson(innloggetBrukerToken, handling, bruker)) {
            if (INNLOGGINGSNIVÅ_3 == authContextHolder.hentInnloggingsnivå()) throw AutorisasjonLevel3Exception("Bruker er innlogget på nivå 3. $handling-tilgang til ekstern bruker som krever nivå 4-innlogging.")
            throw AutorisasjonException("Bruker mangler $handling-tilgang til ekstern bruker")
        }
    }

    private fun rolle(): UserRole = authContextHolder.role.orElseThrow { IllegalStateException("Ingen role funnet") }


    private fun AuthContextHolder.hentInnloggingsnivå(): String {
        return idTokenClaims.flatMap { getStringClaim(it, "acr") }
            .also { logger.info("Fant innloggsnivå med nivå $it") }
            .orElseThrow { AutorisasjonValideringException("Fant ikke innloggingsnivå i token (acr claim) for innlogget personbruker") }
    }

    private fun AuthContextHolder.hentFoedselsnummer(): Foedselsnummer {
        return idTokenClaims.flatMap { getStringClaim(it, ID_PORTEN_PID_CLAIM) }.map(::Foedselsnummer)
            .orElseThrow { AutorisasjonValideringException("Fant ikke fødselsnummer i token (pid claim) for innlogget personbruker") }
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
        private val INNLOGGINGSNIVÅ_3 = "Level3"
        private val INNLOGGINGSNIVÅ_4 = "Level4"
    }
}
