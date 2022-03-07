package no.nav.fo.veilarbregistrering.migrering

interface MigreringRepository {
    fun nesteFraTabell(tabellNavn: TabellNavn, id: Long): List<Map<String, Any>>
    fun hentStatus(): List<Map<String, Any>>
    fun hentSjekksumFor(tabellNavn: TabellNavn): List<Map<String, Any>>
    fun hentAntallPotensieltOppdaterte(): Int
    fun hentRegistreringTilstander(ider: List<Long>): List<Map<String, Any?>>
}