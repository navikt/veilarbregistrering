package no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter

import no.nav.fo.veilarbregistrering.bruker.pdl.PdlError
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlResponse

data class PdlHentIdenter(val hentIdenter: PdlIdenter)

data class PdlIdenter(val identer: List<PdlIdent>)

data class PdlIdent(val ident: String, val historisk: Boolean, val gruppe: PdlGruppe)

enum class PdlGruppe {
    FOLKEREGISTERIDENT, AKTORID, NPID
}

data class PdlHentIdenterRequest(val query: String, val variables: HentIdenterVariables)

class HentIdenterVariables(val ident: String)

class PdlHentIdenterResponse(val data: PdlHentIdenter, override val errors: List<PdlError>?) : PdlResponse
