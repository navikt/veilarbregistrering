package no.nav.fo.veilarbregistrering.autorisasjon

import io.micrometer.core.instrument.Tag
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.autitLogger
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService


open class SystembrukerAutorisasjonService(
    private val authContextHolder: AuthContextHolder,
    private val metricsService: MetricsService) : AutorisasjonService {

    override fun sjekkLesetilgangTilBrukerMedNivå3(bruker: Bruker, cefMelding: CefMelding) {
        throw AutorisasjonValideringException("Kan ikke utføre tilgangskontroll på nivå 3 for systembruker")
    }

    override fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer) = sjekkLesetilgangTilBruker()
    override fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer) = sjekkSkrivetilgangTilBruker()

    override fun sjekkSkrivetilgangTilBrukerForSystembruker(fnr: Foedselsnummer, cefMelding: CefMelding) {
        autitLogger.info(cefMelding.cefMessage())
        sjekkSkrivetilgangTilBruker()
    }

    private fun sjekkLesetilgangTilBruker() {
        logger.info("harTilgangTilPerson utfører ${ActionId.READ} for ${UserRole.SYSTEM}-rolle")
        if (rolle() != UserRole.SYSTEM) throw AutorisasjonValideringException("Kan ikke utføre tilgangskontroll for systembruker med rolle ${rolle()}")
        registrerAutorisationEvent(ActionId.READ)
    }

    private fun rolle(): UserRole = authContextHolder.role.orElseThrow { IllegalStateException("Ingen role funnet") }

    private fun sjekkSkrivetilgangTilBruker() {
        logger.info("harTilgangTilPerson utfører ${ActionId.WRITE} for ${UserRole.SYSTEM}-rolle")
        if (rolle() != UserRole.SYSTEM) throw AutorisasjonValideringException("Kan ikke utføre tilgangskontroll for systembruker med rolle ${rolle()}")
        registrerAutorisationEvent(ActionId.WRITE)
        throw AutorisasjonValideringException("Systembruker har ikke skrivetilgang til bruker")
    }

    private fun registrerAutorisationEvent(handling: ActionId) {
        metricsService.registrer(
            Events.AUTORISASJON,
            Tag.of("navident", "false"),
            Tag.of("handling", handling.id),
            Tag.of("rolle", UserRole.SYSTEM.name.lowercase())
        )
    }

    override val innloggetVeilederIdent: String
        get() {
            throw AutorisasjonValideringException("Kan ikke hente veilederIdent i tilgangskontroll for ${UserRole.SYSTEM}")
        }

    override fun erVeileder(): Boolean = false
}