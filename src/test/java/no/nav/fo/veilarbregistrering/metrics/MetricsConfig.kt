package no.nav.fo.veilarbregistrering.metrics

import io.mockk.mockk
import no.nav.common.metrics.MetricsClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {
    @Bean
    fun metricsClient(): MetricsClient = mockk(relaxed = true)

}