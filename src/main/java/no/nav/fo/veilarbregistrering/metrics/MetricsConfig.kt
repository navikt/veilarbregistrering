package no.nav.fo.veilarbregistrering.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MetricsConfig {

    @Bean
    fun meterRegistry() {
        PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    }

    @Bean
    fun prometheusMetricsService(meterRegistry: MeterRegistry): PrometheusMetricsService =
        PrometheusMetricsService(meterRegistry)

}