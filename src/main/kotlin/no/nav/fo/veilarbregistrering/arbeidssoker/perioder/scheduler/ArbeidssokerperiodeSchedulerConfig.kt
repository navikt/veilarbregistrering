package no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler

import io.getunleash.Unleash
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
        unleashClient: Unleash
    ): ArbeidssokerperiodeScheduler {
        return ArbeidssokerperiodeScheduler(
            leaderElectionClient,
            arbeidssokerperiodeService,
            arbeidssokerperiodeProducer,
            unleashClient
        )
    }
}
