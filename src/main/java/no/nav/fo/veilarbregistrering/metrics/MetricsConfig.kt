package no.nav.fo.veilarbregistrering.metrics

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MetricsConfig {

    @Bean
    fun prometheusMetricsService(meterRegistry: MeterRegistry): PrometheusMetricsService =
        PrometheusMetricsService(meterRegistry)

}