package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.common.job.leader_election.LeaderElectionHttpClient
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class PubliseringSchedulerConfig {

    @Bean
    fun leaderElectionClient(): LeaderElectionClient {
        return LeaderElectionHttpClient()
    }

    @Bean
    fun publiseringAvRegistreringEventsScheduler(
            publiseringAvEventsService: PubliseringAvEventsService,
            leaderElectionClient: LeaderElectionClient,
            unleashClient: UnleashClient
    ): PubliseringAvRegistreringEventsScheduler {
        return PubliseringAvRegistreringEventsScheduler(
                publiseringAvEventsService,
                leaderElectionClient,
                unleashClient)
    }
}