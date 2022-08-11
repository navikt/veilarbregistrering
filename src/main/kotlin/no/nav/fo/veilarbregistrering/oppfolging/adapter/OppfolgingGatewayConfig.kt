package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.VeilarbarenaClient
import no.nav.fo.veilarbregistrering.tokenveksling.DownstreamApi
import no.nav.fo.veilarbregistrering.tokenveksling.TokenExchangeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppfolgingGatewayConfig {

    @Bean
    fun oppfolgingClient(
        objectMapper: ObjectMapper,
        metricsService: MetricsService,
        tokenProvider: ServiceToServiceTokenProvider,
        tokenExchangeService: TokenExchangeService,
    ): OppfolgingClient {
        val propertyName = "VEILARBOPPFOLGINGAPI_URL"

        return OppfolgingClient(
            objectMapper,
            metricsService,
            requireProperty(propertyName),
            tokenExchangeService,
        ) {
            tokenProvider.getServiceToken(
                oppfolgingAppNavn,
                oppfolgingNamespace,
                oppfolgingCluster
            )
        }
    }

    @Bean
    fun veilarbarenaClient(
        tokenProvider: ServiceToServiceTokenProvider,
        metricsService: MetricsService
    ): VeilarbarenaClient {
        val baseUrl = requireProperty("VEILARBARENA_URL")
        val veilarbarenaCluster = requireProperty("VEILARBARENA_CLUSTER")
        val veilarbarenaTokenProvider = {
            try {
                tokenProvider.getServiceToken(
                    "veilarbarena",
                    "pto",
                    veilarbarenaCluster
                )
            } catch (e: Exception) {
                "no token"
            }
        }
        val proxyTokenProvider = {
            tokenProvider.getServiceToken(
                "paw-proxy",
                "paw",
                if (isDevelopment()) "dev-fss" else "prod-fss"
            )
        }
        return VeilarbarenaClient(baseUrl, metricsService, veilarbarenaTokenProvider, proxyTokenProvider)
    }

    @Bean
    fun oppfolgingGateway(
        oppfolgingClient: OppfolgingClient,
        veilarbarenaClient: VeilarbarenaClient
    ): OppfolgingGateway {
        return OppfolgingGatewayImpl(oppfolgingClient, veilarbarenaClient)
    }
}

val oppfolgingNamespace = "pto"
val oppfolgingAppNavn = "veilarboppfolging"
val oppfolgingCluster = requireProperty("VEILARBOPPFOLGINGAPI_CLUSTER")
val oppfolgingApi = DownstreamApi(oppfolgingCluster, oppfolgingNamespace, oppfolgingAppNavn)
