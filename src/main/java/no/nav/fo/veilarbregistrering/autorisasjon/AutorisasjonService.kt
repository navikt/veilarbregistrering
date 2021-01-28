package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.Fnr
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class AutorisasjonService(private val veilarbPep: Pep) {

    fun erInternBruker(): Boolean {
        return AuthContextHolder.erInternBruker()
    }

    fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer) = veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.READ, Fnr(fnr.stringValue()))

    fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer) = veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.WRITE, Fnr(fnr.stringValue()))

    fun sjekkLesetilgangMedAktorId(aktorId: no.nav.fo.veilarbregistrering.bruker.AktorId) {
        if (!veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.READ, AktorId.of(aktorId.asString()))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }

    fun sjekkSkrivetilgangMedAktorId(aktorId: no.nav.fo.veilarbregistrering.bruker.AktorId) {
        if (!veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.WRITE, AktorId.of(aktorId.asString()))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }

    private val innloggetBrukerToken: String
        get() = AuthContextHolder.getIdTokenString()
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Fant ikke token for innlogget bruker") }

    // NAV ident, fnr eller annen ID
    val innloggetBrukerIdent: String
        get() = AuthContextHolder.getSubject()
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "NAV ident is missing") }

    val innloggetVeilederIdent: String
        get() {
            if (!erInternBruker()) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            }
            return innloggetBrukerIdent
        }

    fun erVeileder(): Boolean = erInternBruker()
}