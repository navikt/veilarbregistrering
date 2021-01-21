package no.nav.fo.veilarbregistrering.config

import io.mockk.mockk
import no.nav.common.leaderelection.LeaderElectionClient
import no.nav.common.leaderelection.LeaderElectionHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LeaderElectionConfig {
    @Bean
    fun leaderElectionClient(): LeaderElectionClient = mockk(relaxed = true)
}