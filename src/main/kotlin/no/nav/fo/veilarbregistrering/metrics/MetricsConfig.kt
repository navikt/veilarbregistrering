package no.nav.fo.veilarbregistrering.metrics

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MetricsConfig {

    @Bean
    fun prometheusMetricsService(): MetricsService =
        PrometheusMetricsService()
}