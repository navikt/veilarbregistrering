package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.metrics.MetricsClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppfolgingGatewayConfig {

    @Bean
    fun oppfolgingClient(
            objectMapper: ObjectMapper,
            metricsClient: MetricsClient,
            systemUserTokenProvider: SystemUserTokenProvider): OppfolgingClient {
        return OppfolgingClient(
                objectMapper,
                metricsClient,
                EnvironmentUtils.getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME),
                systemUserTokenProvider)
    }

    @Bean
    fun oppfolgingGateway(oppfolgingClient: OppfolgingClient): OppfolgingGateway {
        return OppfolgingGatewayImpl(oppfolgingClient)
    }

    companion object {
        const val OPPFOLGING_API_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL"
    }
}