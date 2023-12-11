package no.nav.fo.veilarbregistrering.registrering.ordinaer.scheduler

import io.getunleash.Unleash
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class OpplysningerMottattSchedulerConfig {
    @Bean
    fun opplysningMottattScheduler(
        leaderElectionClient: LeaderElectionClient,
        registreringService: BrukerRegistreringService,
        opplysningerMottattProducer: OpplysningerMottattProducer,
        unleashClient: Unleash
    ): OpplysningMottattScheduler {
        return OpplysningMottattScheduler(
            leaderElectionClient,
            registreringService,
            opplysningerMottattProducer,
            unleashClient
        )
    }
}
