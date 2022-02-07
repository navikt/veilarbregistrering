package no.nav.fo.veilarbregistrering.orgenhet.adapter

data class RsNavKontorDto (val enhetNr: String?, val status: String?) {
    constructor() : this(null, null)
}
