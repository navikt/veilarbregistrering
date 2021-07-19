package no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt

import no.nav.fo.veilarbregistrering.bruker.pdl.PdlError
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlResponse

enum class PdlGtType {
    KOMMUNE,
    BYDEL,
    UTLAND,
    UDEFINERT;
}

data class PdlGeografiskTilknytning(
    val gtType: PdlGtType,
    val gtKommune: String?,
    val gtBydel: String?,
    val gtLand: String?
)

data class PdlHentGeografiskTilknytningResponse(
    val data: PdlHentGeografiskTilknytning,
    private val errors: MutableList<PdlError>
) :
    PdlResponse {
    override fun getErrors() = errors
}

data class PdlHentGeografiskTilknytningRequest(val query: String, val variables: HentGeografiskTilknytningVariables)

data class HentGeografiskTilknytningVariables (val ident: String)

data class PdlHentGeografiskTilknytning(val hentGeografiskTilknytning: PdlGeografiskTilknytning)
