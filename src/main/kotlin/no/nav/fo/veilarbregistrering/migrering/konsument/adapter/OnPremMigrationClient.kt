package no.nav.fo.veilarbregistrering.migrering.konsument.adapter

import no.nav.fo.veilarbregistrering.migrering.TabellNavn
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateClient
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand

class OnPremMigrationClient: MigrateClient {
    override fun hentNesteBatchFraTabell(tabell: TabellNavn, sisteIndex: Int): List<MutableMap<String, Any>> {
        throw IllegalStateException("MigrateClient forsøkt benyttet i FSS")
    }

    override fun hentSjekkerForTabell(tabell: TabellNavn): List<Map<String, Any>> {
        throw IllegalStateException("MigrateClient forsøkt benyttet i FSS")
    }

    override fun hentSjekkerForProfilering(): List<Long> {
        throw IllegalStateException("MigrateClient forsøkt benyttet i FSS")
    }

    override fun hentAntallPotensieltOppdaterteTilstander(): Int {
        throw IllegalStateException("MigrateClient forsøkt benyttet i FSS")
    }

    override fun hentOppdaterteRegistreringStatuser(trengerOppdatering: List<RegistreringTilstand>): List<Map<String, Any>> {
        throw IllegalStateException("MigrateClient forsøkt benyttet i FSS")
    }
}