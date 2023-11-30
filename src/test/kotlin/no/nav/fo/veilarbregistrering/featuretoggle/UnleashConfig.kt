package no.nav.fo.veilarbregistrering.featuretoggle

import io.getunleash.Unleash
import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UnleashConfig {
    @Bean
    fun unleashClient(): Unleash = mockk(relaxed = true)
}
