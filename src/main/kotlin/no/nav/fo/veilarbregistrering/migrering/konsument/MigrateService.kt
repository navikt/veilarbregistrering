package no.nav.fo.veilarbregistrering.migrering.konsument

import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.TabellNavn

class MigrateService(
    private val repository: MigrateRepository,
    private val migrateClient: MigrateClient
) {

    fun migrate() {
        TabellNavn.values().forEach {
            val sisteIndex = repository.hentStoersteId(it)
            val rader = migrateClient.hentNesteBatchFraTabell(it, sisteIndex)
            repository.settInnRader(it, rader)
        }

        val antallSomKanTrengeOppdatering = repository.antallRaderSomKanTrengeOppdatering()
        if (migrateClient.hentAntallPotensieltOppdaterteTilstander() != antallSomKanTrengeOppdatering) {
            hentOgOppdaterRegistreringTilstander()
        }
    }

    private fun hentOgOppdaterRegistreringTilstander() {
        val trengerOppdatering = repository.hentRaderSomKanTrengeOppdatering()

        val rader = migrateClient.hentOppdaterteRegistreringStatuser(trengerOppdatering)

        logger.info("Hentet oppdaterte rader:", rader)
        val antallOppdaterte = repository.oppdaterTilstander(rader)

        if (rader.size == antallOppdaterte.size) logger.info("Oppdaterte ${antallOppdaterte.size} rader")
        else logger.warn("Oppdaterte ${antallOppdaterte.size} rader, men mottok ${rader.size} fra oracle")
    }
}