package no.nav.fo.veilarbregistrering.migrering.konsument

import no.nav.fo.veilarbregistrering.migrering.TabellNavn
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand

interface MigrateRepository {
    fun hentStoersteId(tabellNavn: TabellNavn): Int
    fun settInnRader(tabell: TabellNavn, rader: List<MutableMap<String, Any>>)
    fun antallRaderSomKanTrengeOppdatering(): Int
    fun hentRaderSomKanTrengeOppdatering(): List<RegistreringTilstand>
    fun hentSjekkerForTabell(tabellNavn: TabellNavn): List<Map<String, Any>>
    fun oppdaterTilstander(tilstander: List<Map<String, Any>>): List<Int>
}