package no.nav.fo.veilarbregistrering.bruker

interface PdlOppslagGateway {
    fun hentPerson(aktorid: AktorId): Person?
    fun hentIdenter(fnr: Foedselsnummer): Identer
    fun hentIdenter(aktorId: AktorId): Identer
    fun hentGeografiskTilknytning(aktorId: AktorId): GeografiskTilknytning?
}
