package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

class TilgangskontrollService(
    private val authContextHolder: AuthContextHolder,
    private val autorisasjonServiceMap: Map<UserRole, AutorisasjonService>
) {

    fun sjekkLesetilgangTilBrukerMedNivå3(bruker: Bruker, cefMelding: CefMelding) {
        autorisasjonServiceMap[hentRolle()]?.sjekkLesetilgangTilBrukerMedNivå3(bruker, cefMelding)
            ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
    }

    fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer) {
        autorisasjonServiceMap[hentRolle()]?.sjekkLesetilgangTilBruker(fnr)
            ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
    }

    fun sjekkSkrivetilgangTilBrukerForSystem(fnr: Foedselsnummer, cefMelding: CefMelding) {
        autorisasjonServiceMap[hentRolle()]?.sjekkSkrivetilgangTilBrukerForSystembruker(fnr, cefMelding)
            ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
    }

    fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer) {
        autorisasjonServiceMap[hentRolle()]?.sjekkSkrivetilgangTilBruker(fnr)
            ?: throw AutorisasjonValideringException("Fant ikke tilgangskontroll for rollen ${hentRolle()}")
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