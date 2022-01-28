package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.Fnr
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

open class AutorisasjonService(private val veilarbPep: Pep, private val authContextHolder: AuthContextHolder) {

    fun erInternBruker(): Boolean {
        return authContextHolder.erInternBruker()
    }

    private fun rolle(): UserRole = authContextHolder.role.orElseThrow { IllegalStateException("Ingen role funnet") }

    fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer) {
        val ident = authContextHolder.navIdent.orElse(null)
        val rolle = rolle()

        val harTilgang =
            if (ident != null && rolle == UserRole.INTERN) {
                veilarbPep.harVeilederTilgangTilPerson(ident, ActionId.READ, Fnr(fnr.stringValue()))
            } else {
                veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.READ, Fnr(fnr.stringValue()))
            }

        if (!harTilgang) {
            throw IllegalStateException("Veileder har ikke lesetilgang til bruker")
        }
    }

    fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer) =
        veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.WRITE, Fnr(fnr.stringValue()))


    fun sjekkLesetilgangMedAktorId(aktorId: no.nav.fo.veilarbregistrering.bruker.AktorId) {
        if (authContextHolder.role.orElse(null) == UserRole.SYSTEM) return
        if (!veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.READ, AktorId(aktorId.aktorId))) {

            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }

    fun sjekkSkrivetilgangMedAktorId(aktorId: no.nav.fo.veilarbregistrering.bruker.AktorId) {
        if (!veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.WRITE, AktorId(aktorId.aktorId))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
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
            return innloggetBrukerIdent
        }

    fun erVeileder(): Boolean = erInternBruker()
}