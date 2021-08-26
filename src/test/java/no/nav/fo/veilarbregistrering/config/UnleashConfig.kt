package no.nav.fo.veilarbregistrering.config

import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UnleashConfig {
    @Bean
    fun unleashClient(): UnleashClient = mockk(relaxed = true)
}
