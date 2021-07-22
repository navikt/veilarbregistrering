package no.nav.fo.veilarbregistrering.bruker.pdl

data class PdlError(
    val message: String? = null,
    val locations: List<PdlErrorLocation>? = null,
    val path: List<String>? = null,
    val extensions: PdlErrorExtension? = null
)

data class PdlErrorLocation(
    val line: Int? = null,
    val column: Int? = null
)

data class PdlErrorExtension(
    val code: String? = null,
    val classification: String? = null,
    val details: PdlErrorDetails? = null
)
