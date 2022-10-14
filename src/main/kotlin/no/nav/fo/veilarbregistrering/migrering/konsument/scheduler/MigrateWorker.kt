package no.nav.fo.veilarbregistrering.migrering.konsument.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateService
import org.springframework.scheduling.annotation.Scheduled

class MigrateWorker(
    private val leaderElectionClient: LeaderElectionClient,
    private val migrateService: MigrateService
) {

    @Scheduled(cron = HVERT_TJUENDE_MINUTT)
    fun migrate() {
        if (isOnPrem()) {
            logger.warn("Migreringsjobb for GCP ble forsøkt kjørt fra FSS")
            return
        }

        if (!leaderElectionClient.isLeader) {
            return
        }

        migrateService.migrate()
    }

    companion object {
        const val HVERT_TJUENDE_MINUTT = "0 */20 * * * *"
    }

}