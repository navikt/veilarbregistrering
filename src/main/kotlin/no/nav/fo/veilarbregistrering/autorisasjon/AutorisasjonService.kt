package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface AutorisasjonService {
    fun sjekkLesetilgangTilBruker(bruker: Foedselsnummer)
    fun sjekkSkrivetilgangTilBruker(bruker: Foedselsnummer)

    fun erVeileder(): Boolean
    val innloggetVeilederIdent: String
}