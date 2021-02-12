package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler

import io.mockk.mockk
import no.nav.common.job.leader_election.LeaderElectionClient
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PubliseringSchedulerConfig {

    @Bean
    fun leaderElectionClient(): LeaderElectionClient = mockk(relaxed = true)

    @Bean
    fun publiseringAvRegistreringEventsScheduler(): PubliseringAvRegistreringEventsScheduler {
        return Mockito.mock(PubliseringAvRegistreringEventsScheduler::class.java)
    }
}