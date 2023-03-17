package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface AutorisasjonService {

    fun sjekkLesetilgangTilBrukerMedNivå3(bruker: Bruker, cefMelding: CefMelding)
    fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer)
    fun sjekkLesetilgangTilBruker(bruker: Bruker, cefMelding: CefMelding)
    fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer)
    fun sjekkSkrivetilgangTilBruker(bruker: Bruker, cefMelding: CefMelding)

    fun sjekkSkrivetilgangTilBrukerForSystembruker(fnr: Foedselsnummer, cefMelding: CefMelding)

    fun erVeileder(): Boolean

    val innloggetVeilederIdent: String
}