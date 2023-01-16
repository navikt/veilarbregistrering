package no.nav.fo.veilarbregistrering.migrering.tilbyder

import no.nav.fo.veilarbregistrering.migrering.TabellNavn

interface MigreringRepository {
    fun nesteFraTabell(tabellNavn: TabellNavn, id: Long): List<Map<String, Any>>
    fun hentStatus(): List<Map<String, Any>>
    fun hentSjekksumFor(tabellNavn: TabellNavn): List<Map<String, Any>>
    fun hentSjekkForProfilering(): List<Long>
    fun hentAntallPotensieltOppdaterte(): Int
    fun hentRegistreringTilstander(ider: List<Long>): List<Map<String, Any?>>
}