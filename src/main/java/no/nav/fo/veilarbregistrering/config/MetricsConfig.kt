package no.nav.fo.veilarbregistrering.config

import no.nav.common.metrics.InfluxClient
import no.nav.common.metrics.MetricsClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {
    @Bean
    fun metricsClient(): MetricsClient =
        InfluxClient()

}