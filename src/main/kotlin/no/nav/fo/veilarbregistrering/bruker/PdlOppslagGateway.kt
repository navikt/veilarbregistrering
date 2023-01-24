package no.nav.fo.veilarbregistrering.bruker

interface PdlOppslagGateway {
    fun hentPerson(aktorid: AktorId): Person?
    fun hentIdenter(fnr: Foedselsnummer, erSystemKontekst: Boolean = false): Identer
    fun hentIdenter(aktorId: AktorId): Identer
    fun hentIdenterBolk(fnrListe: List<Foedselsnummer>, erSystemKontekst: Boolean = false): Map<Foedselsnummer, AktorId>
    fun hentGeografiskTilknytning(aktorId: AktorId): GeografiskTilknytning?
}
