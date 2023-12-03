package no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class ArbeidssokerperiodeSchedulerConfig {
    @Bean
    fun arbeidssokerperiodeScheduler(
        leaderElectionClient: LeaderElectionClient,
        arbeidssokerperiodeService: ArbeidssokerperiodeService,
        arbeidssokerperiodeProducer: ArbeidssokerperiodeProducer,
    ): ArbeidssokerperiodeScheduler {
        return ArbeidssokerperiodeScheduler(
            leaderElectionClient,
            arbeidssokerperiodeService,
            arbeidssokerperiodeProducer,
        )
    }
}
