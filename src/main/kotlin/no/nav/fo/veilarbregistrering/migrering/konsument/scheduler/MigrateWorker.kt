package no.nav.fo.veilarbregistrering.migrering.konsument.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.TabellNavn
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateClient
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateRepository

class MigrateWorker(
    private val leaderElectionClient: LeaderElectionClient,
    private val repository: MigrateRepository,
    private val migrateClient: MigrateClient
) {

    //@Scheduled(fixedDelay = 20000)
    fun migrate() {
        // Lese pg-db, finne tabeller og kolonnenavn
        // Autorisasjon (header som leses) - hentes i veilarbregistrering fra Google secret manager
        // Proxy-er

        if (!leaderElectionClient.isLeader) {
            return
        }

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

    fun hentOgOppdaterRegistreringTilstander() {
        val trengerOppdatering = repository.hentRaderSomKanTrengeOppdatering()

        val rader = migrateClient.hentOppdaterteRegistreringStatuser(trengerOppdatering)

        logger.info("Hentet oppdaterte rader:", rader)
        val antallOppdaterte = repository.oppdaterTilstander(rader)

        if (rader.size == antallOppdaterte.size) logger.info("Oppdaterte ${antallOppdaterte.size} rader")
        else logger.warn("Oppdaterte ${antallOppdaterte.size} rader, men mottok ${rader.size} fra oracle")
    }
}