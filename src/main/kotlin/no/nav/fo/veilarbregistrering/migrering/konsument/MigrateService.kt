package no.nav.fo.veilarbregistrering.migrering.konsument

import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.TabellNavn
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class MigrateService(
    private val repository: MigrateRepository,
    private val migrateClient: MigrateClient
) {

    @ExperimentalTime
    fun migrate() {
        TabellNavn.values().forEach {
            val sisteIndex = repository.hentStoersteId(it)
            val (rader, tidsbruk) = measureTimedValue { migrateClient.hentNesteBatchFraTabell(it, sisteIndex) }
            logger.info("Hentet ${rader.size} rader fra tabell $it i FSS-app. Tidsbruk: ${tidsbruk.inWholeSeconds} sekunder")
            val insertTidsbrukIMin = measureTimeMillis {
                repository.settInnRader(it, rader)
            }.toFloat()/60000.0
            logger.info("Brukte $insertTidsbrukIMin minutter på å sette inn ${rader.size} i tabell $it")
        }

        val antallSomKanTrengeOppdatering = repository.antallRaderSomKanTrengeOppdatering()
        if (migrateClient.hentAntallPotensieltOppdaterteTilstander() != antallSomKanTrengeOppdatering) {
            hentOgOppdaterRegistreringTilstander()
        } else {
            logger.info("Fant ingen nye tilstander som trenger oppdatering i migreringsjobb")
        }
    }

    @ExperimentalTime
    private fun hentOgOppdaterRegistreringTilstander() {
        val trengerOppdatering = repository.hentRaderSomKanTrengeOppdatering()

        val (rader, tidsbruk) = measureTimedValue { migrateClient.hentOppdaterteRegistreringStatuser(trengerOppdatering) }
        logger.info("Hentet oppdaterte rader: ${rader.size} fra registrering_tilstand i FSS-app. Tidsbruk: ${tidsbruk.inWholeSeconds} sekunder")

        val (antallOppdaterte, tidsbruk2) = measureTimedValue { repository.oppdaterTilstander(rader) }
        logger.info("Oppdaterte $antallOppdaterte rader i registrering_tilstand. Tidsbruk: ${tidsbruk2.inWholeSeconds} sekunder")

        if (rader.size == antallOppdaterte.size) logger.info("Oppdaterte ${antallOppdaterte.size} rader")
        else logger.warn("Oppdaterte ${antallOppdaterte.size} rader, men mottok ${rader.size} fra oracle")
    }
}