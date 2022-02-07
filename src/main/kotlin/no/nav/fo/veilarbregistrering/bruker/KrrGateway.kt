package no.nav.fo.veilarbregistrering.bruker

interface KrrGateway {
    fun hentKontaktinfo(bruker: Bruker): Telefonnummer?
}