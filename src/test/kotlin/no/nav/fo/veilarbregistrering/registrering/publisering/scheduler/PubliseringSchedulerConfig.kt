package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler

import io.mockk.every
import io.mockk.mockk
import no.nav.common.job.leader_election.LeaderElectionClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PubliseringSchedulerConfig {

    @Bean
    fun leaderElectionClient(): LeaderElectionClient =
        mockk<LeaderElectionClient>(relaxed = true).also { every { it.isLeader } returns true }

    @Bean
    fun publiseringAvRegistreringEventsScheduler(): PubliseringAvRegistreringEventsScheduler =
        mockk(relaxed = true)
}