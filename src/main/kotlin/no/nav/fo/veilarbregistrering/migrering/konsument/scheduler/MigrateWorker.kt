package no.nav.fo.veilarbregistrering.migrering.konsument.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateService

class MigrateWorker(
    private val leaderElectionClient: LeaderElectionClient,
    private val migrateService: MigrateService
) {

    //@Scheduled(fixedDelay = 20000)
    fun migrate() {
        // Lese pg-db, finne tabeller og kolonnenavn
        // Autorisasjon (header som leses) - hentes i veilarbregistrering fra Google secret manager
        // Proxy-er

        if (!leaderElectionClient.isLeader) {
            return
        }

        migrateService.migrate()
    }
}