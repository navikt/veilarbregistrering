package no.nav.fo.veilarbregistrering.migrering.konsument.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateService
import org.springframework.scheduling.annotation.Scheduled
import kotlin.system.measureTimeMillis

class MigrateWorker(
    private val leaderElectionClient: LeaderElectionClient,
    private val migrateService: MigrateService
) {

    @Scheduled(cron = HVERT_TIENDE_MINUTT)
    fun migrate() {
        if (isOnPrem()) {
            logger.warn("Migreringsjobb for GCP ble forsøkt kjørt fra FSS")
            return
        }

        if (!leaderElectionClient.isLeader) {
            return
        }
        logger.info("Kjører migreringsjobb for data fra Oracle til Postgres")
        val tidBrukt = measureTimeMillis {
            migrateService.migrate()
        }
        val antallMinutterBrukt = tidBrukt.toFloat()/60000.0
        logger.info("Migreringsjobb tok $antallMinutterBrukt minutter")
    }

    companion object {
        const val HVERT_TIENDE_MINUTT = "0 */10 * * * *"
    }

}