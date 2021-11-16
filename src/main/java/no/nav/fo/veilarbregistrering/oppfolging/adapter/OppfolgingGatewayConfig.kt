package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppfolgingGatewayConfig {

    @Bean
    fun oppfolgingClient(
        objectMapper: ObjectMapper,
        prometheusMetricsService: PrometheusMetricsService,
        tokenProvider: ServiceToServiceTokenProvider,
    ): OppfolgingClient {
        val propertyName = "VEILARBOPPFOLGINGAPI_URL"
        val clusterPropertyName = "VEILARBOPPFOLGINGAPI_CLUSTER"
        val cluster = requireProperty(clusterPropertyName)

        return OppfolgingClient(
            objectMapper,
            prometheusMetricsService,
            requireProperty(propertyName),
        ) {
            tokenProvider.getServiceToken(
                "veilarboppfolging",
                "pto",
                cluster
            )
        }
    }

    @Bean
    fun oppfolgingGateway(oppfolgingClient: OppfolgingClient): OppfolgingGateway {
        return OppfolgingGatewayImpl(oppfolgingClient)
    }
}