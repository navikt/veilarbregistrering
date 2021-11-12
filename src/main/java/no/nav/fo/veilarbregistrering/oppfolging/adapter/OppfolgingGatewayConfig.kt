package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
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
        systemUserTokenProvider: SystemUserTokenProvider,
        tokenProvider: ServiceToServiceTokenProvider,
    ): OppfolgingClient {
        val propertyName = "VEILARBOPPFOLGINGAPI_URL"
        val clusterPropertyName = "VEILARBOPPFOLGINGAPI_CLUSTER"
        val cluster =             EnvironmentUtils.getRequiredProperty(clusterPropertyName)

        return OppfolgingClient(
            objectMapper,
            prometheusMetricsService,
            EnvironmentUtils.getRequiredProperty(propertyName),
            systemUserTokenProvider
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