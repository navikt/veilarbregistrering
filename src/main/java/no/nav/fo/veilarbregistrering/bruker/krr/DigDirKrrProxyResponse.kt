package no.nav.fo.veilarbregistrering.bruker.krr

internal data class DigDirKrrProxyResponse(
    val personident: String,
    val aktiv : Boolean,
    val reservert : Boolean,
    val mobiltelefonnummer : String?)