package no.nav.fo.veilarbregistrering.bruker.pdl

interface PdlResponse {
    val errors: List<PdlError>?
}
