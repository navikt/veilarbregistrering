package no.nav.fo.veilarbregistrering.migrering.konsument

import no.nav.fo.veilarbregistrering.migrering.TabellNavn
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand

interface MigrateClient {
    fun hentNesteBatchFraTabell(tabell: TabellNavn, sisteIndex: Int): List<MutableMap<String, Any>>
    fun hentSjekkerForTabell(tabell: TabellNavn): List<Map<String, Any>>
    fun hentAntallPotensieltOppdaterteTilstander(): Int
    fun hentOppdaterteRegistreringStatuser(trengerOppdatering: List<RegistreringTilstand>): List<Map<String, Any>>
}