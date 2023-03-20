package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface AutorisasjonService {

    fun sjekkLesetilgangTilBrukerMedNivå3(bruker: Bruker, kontekst: String)
    fun sjekkLesetilgangTilBruker(fnr: Foedselsnummer)
    fun sjekkLesetilgangTilBruker(bruker: Bruker, kontekst: String)
    fun sjekkSkrivetilgangTilBruker(fnr: Foedselsnummer)
    fun sjekkSkrivetilgangTilBruker(bruker: Bruker, kontekst: String)

    fun sjekkSkrivetilgangTilBrukerForSystembruker(fnr: Foedselsnummer, cefMelding: CefMelding)

    fun erVeileder(): Boolean

    val innloggetVeilederIdent: String
}