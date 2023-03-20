package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

class TilgangskontrollService(
    private val authContextHolder: AuthContextHolder,
    private val autorisasjonServiceMap: Map<UserRole, AutorisasjonService>
) {

    fun sjekkLesetilgangTilBrukerMedNivå3(bruker: Bruker, kontekst: String) {
        autorisasjonServiceMap[hentRolle()]?.sjekkLesetilgangTilBrukerMedNivå3(bruker, kontekst)
            ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
    }

    fun sjekkLesetilgangTilBruker(bruker: Bruker, kontekst: String) {
        if (hentRolle() == UserRole.EKSTERN) {
            autorisasjonServiceMap[hentRolle()]?.sjekkLesetilgangTilBruker(bruker, kontekst)
        } else {
            autorisasjonServiceMap[hentRolle()]?.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
                ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
        }
    }

    fun sjekkSkrivetilgangTilBrukerForSystem(fnr: Foedselsnummer, cefMelding: CefMelding) {
        autorisasjonServiceMap[hentRolle()]?.sjekkSkrivetilgangTilBrukerForSystembruker(fnr, cefMelding)
            ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
    }

    fun sjekkSkrivetilgangTilBruker(bruker: Bruker, kontekst: String) {
        if (hentRolle() == UserRole.EKSTERN) {
            autorisasjonServiceMap[hentRolle()]?.sjekkSkrivetilgangTilBruker(bruker, kontekst)
        } else {
            autorisasjonServiceMap[hentRolle()]?.sjekkSkrivetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
                ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
        }
    }

    fun erVeileder(): Boolean {
        return autorisasjonServiceMap[hentRolle()]?.erVeileder()
            ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
    }

    val innloggetVeilederIdent: String
        get() {
            return autorisasjonServiceMap[hentRolle()]?.innloggetVeilederIdent
                ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
        }

    private fun hentRolle(): UserRole {
        return authContextHolder.role.orElseThrow { AutorisasjonValideringException("Fant ikke rolle for bruker") }
    }

}