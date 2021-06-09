package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.Fnr
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.loggerFor
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class AutorisasjonService(private val veilarbPep: Pep, private val veilarbPepGammel: Pep, private val authContextHolder: AuthContextHolder) {

    fun erInternBruker(): Boolean {
        return authContextHolder.erInternBruker()
    }

    fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer) = sammenliknTilgangssjekk(innloggetBrukerToken, ActionId.READ, Fnr(fnr.stringValue()))

    fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer) = sammenliknTilgangssjekk(innloggetBrukerToken, ActionId.WRITE, Fnr(fnr.stringValue()))

    fun sjekkLesetilgangMedAktorId(aktorId: no.nav.fo.veilarbregistrering.bruker.AktorId) {
        if (!sammenliknTilgangssjekk(innloggetBrukerToken, ActionId.READ, AktorId.of(aktorId.asString()))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }

    fun sjekkSkrivetilgangMedAktorId(aktorId: no.nav.fo.veilarbregistrering.bruker.AktorId) {
        if (!sammenliknTilgangssjekk(innloggetBrukerToken, ActionId.WRITE, AktorId.of(aktorId.asString()))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }

    private fun sammenliknTilgangssjekk(innloggetBrukerToken: String, actionId: ActionId, aktorId: AktorId): Boolean {
        val ny = veilarbPep.harTilgangTilPerson(innloggetBrukerToken, actionId, aktorId)
        val gammel = veilarbPepGammel.harTilgangTilPerson(innloggetBrukerToken, actionId, aktorId)
        sammenlikn(ny, gammel)
        return ny
    }

    private fun sammenliknTilgangssjekk(innloggetBrukerToken: String, actionId: ActionId, fnr: Fnr): Boolean {
        val ny = veilarbPep.harTilgangTilPerson(innloggetBrukerToken, actionId, fnr)
        val gammel = veilarbPepGammel.harTilgangTilPerson(innloggetBrukerToken, actionId, fnr)
        sammenlikn(ny, gammel)
        return ny
    }

    private fun sammenlikn(ny: Boolean, gammel: Boolean) {
        if (ny != gammel) {
            LOG.warn("usamsvar i tilgangssjekk mot ABAC [gammel] [ny]: [${gammel}] [${ny}]")
        } else LOG.info("Ny og gammel ABAC samsvarer")
    }

    fun erVeileder(): Boolean = erInternBruker()

    private val innloggetBrukerToken: String
        get() = authContextHolder.getIdTokenString()
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Fant ikke token for innlogget bruker") }

    // NAV ident, fnr eller annen ID
    private val innloggetBrukerIdent: String
        get() = authContextHolder.getSubject()
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "NAV ident is missing") }

    val innloggetVeilederIdent: String
        get() {
            if (!erInternBruker()) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            }
            return innloggetBrukerIdent
        }


    companion object {
        private val LOG = loggerFor<AutorisasjonService>()
    }
}