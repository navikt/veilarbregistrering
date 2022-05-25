package no.nav.fo.veilarbregistrering.autorisasjon

import io.micrometer.core.instrument.Tag
import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.Fnr
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


open class PersonbrukerAutorisasjonService(
    private val veilarbPep: Pep,
    private val authContextHolder: AuthContextHolder,
    private val metricsService: MetricsService) : AutorisasjonService {

    override fun sjekkLesetilgangTilBruker(bruker: Foedselsnummer) = sjekkLesetilgangTilBruker(tilEksternId(bruker))
    override fun sjekkSkrivetilgangTilBruker(bruker: Foedselsnummer) = sjekkSkrivetilgangTilBruker(tilEksternId(bruker))

    private fun tilEksternId(bruker: Foedselsnummer) = Fnr(bruker.stringValue())

    private fun sjekkLesetilgangTilBruker(brukerId: EksternBrukerId) {
        validateState()
        if (!harTilgang(ActionId.READ, brukerId)) throw AutorisasjonException("Bruker mangler tilgang til subjektet")
    }

    private fun sjekkSkrivetilgangTilBruker(brukerId: EksternBrukerId) {
        validateState()
        if (!harTilgang(ActionId.WRITE, brukerId)) throw AutorisasjonException("Bruker mangler tilgang til subjektet")
    }

    private fun validateState() {
        if (!authContextHolder.erEksternBruker())
            throw UnsupportedOperationException("Denne strategien skal kun benyttes for ${UserRole.EKSTERN} - ikke ${authContextHolder.role}")
    }

    private fun harTilgang(handling: ActionId, bruker: EksternBrukerId): Boolean {
        LOG.info("harTilgangTilPerson utfører $handling for ${UserRole.EKSTERN}-rolle")
        registrerAutorisationEvent(false, handling, UserRole.EKSTERN)
        return veilarbPep.harTilgangTilPerson(innloggetBrukerToken, handling, bruker)
    }

    private fun registrerAutorisationEvent(navIdentFlagg: Boolean, handling: ActionId, userRole: UserRole) {
        metricsService.registrer(
            Events.AUTORISASJON,
            Tag.of("navident", navIdentFlagg.toString().lowercase())   ,
            Tag.of("handling", handling.id),
            Tag.of("rolle", userRole.name.lowercase())
        )
    }

    private val innloggetBrukerToken: String
        get() = authContextHolder.idTokenString
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Fant ikke token for innlogget bruker") }

    override fun erVeileder(): Boolean = false

    override val innloggetVeilederIdent: String
        get() {
            throw UnsupportedOperationException("InnloggetVeilederIdent er ikke støttet med rollen ${UserRole.EKSTERN}")
        }

    companion object {
        private val LOG = LoggerFactory.getLogger(PersonbrukerAutorisasjonService::class.java)
    }
}