package no.nav.fo.veilarbregistrering.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.mockk.mockk
import no.nav.common.metrics.MetricsClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {
    @Bean
    fun metricsClient(): MetricsClient = mockk(relaxed = true)

    @Bean
    fun influxMetricsService(metricsClient: MetricsClient): InfluxMetricsService {
        return InfluxMetricsService(metricsClient)
    }

    @Bean
    fun prometheusMetricsService(meterRegistry: MeterRegistry): PrometheusMetricsService {
        return PrometheusMetricsService(meterRegistry)
    }
}