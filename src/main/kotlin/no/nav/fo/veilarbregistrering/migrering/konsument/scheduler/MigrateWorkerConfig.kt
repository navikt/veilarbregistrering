package no.nav.fo.veilarbregistrering.migrering.konsument.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@Profile("gcp")
class MigrateWorkerConfig {

    @Bean
    fun migrateWorker(
        leaderElectionClient: LeaderElectionClient,
        migrateService: MigrateService): MigrateWorker
    {
        return MigrateWorker(
            leaderElectionClient,
            migrateService)
    }
}