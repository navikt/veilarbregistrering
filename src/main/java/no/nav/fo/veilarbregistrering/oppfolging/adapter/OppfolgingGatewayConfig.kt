package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.fo.veilarbregistrering.config.requireClusterName
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.oauth2.AadOboService
import no.nav.fo.veilarbregistrering.oauth2.DownstreamApi
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
        aadOboService: AadOboService,
    ): OppfolgingClient {
        val propertyName = "VEILARBOPPFOLGINGAPI_URL"

        return OppfolgingClient(
            objectMapper,
            prometheusMetricsService,
            requireProperty(propertyName),
            aadOboService,
        ) {
            tokenProvider.getServiceToken(
                oppfolgingAppNavn,
                oppfolgingNamespace,
                oppfolgingCluster
            )
        }
    }

    @Bean
    fun oppfolgingGateway(oppfolgingClient: OppfolgingClient): OppfolgingGateway {
        return OppfolgingGatewayImpl(oppfolgingClient)
    }
}

val oppfolgingNamespace = "pto"
val oppfolgingAppNavn = "veilarboppfolging"
val oppfolgingCluster = requireProperty("VEILARBOPPFOLGINGAPI_CLUSTER")
val oppfolgingApi = DownstreamApi(oppfolgingCluster, oppfolgingNamespace, oppfolgingAppNavn)
