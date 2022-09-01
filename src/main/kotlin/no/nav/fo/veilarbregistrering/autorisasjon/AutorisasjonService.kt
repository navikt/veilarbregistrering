package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface AutorisasjonService {

    fun sjekkLesetilgangTilBrukerMedNivå3(fnr: Foedselsnummer)
    fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer)
    fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer)

    fun erVeileder(): Boolean

    val innloggetVeilederIdent: String
}