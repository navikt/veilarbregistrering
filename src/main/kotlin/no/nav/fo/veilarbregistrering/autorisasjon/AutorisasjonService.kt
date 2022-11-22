package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface AutorisasjonService {

    fun sjekkLesetilgangTilBrukerMedNivå3(bruker: Bruker, cefMelding: CefMelding)
    fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer)
    fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer)

    fun erVeileder(): Boolean

    val innloggetVeilederIdent: String
}