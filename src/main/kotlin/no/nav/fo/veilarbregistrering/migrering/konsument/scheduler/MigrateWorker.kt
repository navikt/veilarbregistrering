package no.nav.fo.veilarbregistrering.migrering.konsument.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateService
import org.springframework.scheduling.annotation.Scheduled

class MigrateWorker(
    private val leaderElectionClient: LeaderElectionClient,
    private val migrateService: MigrateService
) {

    @Scheduled(fixedDelay = 20000)
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
}