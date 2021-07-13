package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppfolgingGatewayConfig {

    @Bean
    fun oppfolgingClient(
        objectMapper: ObjectMapper,
        metricsService: InfluxMetricsService,
        systemUserTokenProvider: SystemUserTokenProvider): OppfolgingClient {

        val OPPFOLGING_API_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL"
        return OppfolgingClient(
                objectMapper,
                metricsService,
                EnvironmentUtils.getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME),
                systemUserTokenProvider)
    }

    @Bean
    fun oppfolgingGateway(oppfolgingClient: OppfolgingClient): OppfolgingGateway {
        return OppfolgingGatewayImpl(oppfolgingClient)
    }
}