package no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt

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

data class PdlHentGeografiskTilknytningResponse(val data: PdlHentGeografiskTilknytning)

data class PdlHentGeografiskTilknytningRequest(val query: String, val variables: HentGeografiskTilknytningVariables)

data class HentGeografiskTilknytningVariables(val ident: String)

data class PdlHentGeografiskTilknytning(val hentGeografiskTilknytning: PdlGeografiskTilknytning)
