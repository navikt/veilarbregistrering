package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.autentisering.tokenveksling.DownstreamApi
import no.nav.fo.veilarbregistrering.autentisering.tokenveksling.TokenExchangeService
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.VeilarbarenaClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppfolgingGatewayConfig {

    @Bean
    fun oppfolgingClient(
        objectMapper: ObjectMapper,
        metricsService: MetricsService,
        tokenProvider: AzureAdMachineToMachineTokenClient,
        tokenExchangeService: TokenExchangeService,
    ): OppfolgingClient {
        val propertyName = "VEILARBOPPFOLGINGAPI_URL"

        return OppfolgingClient(
            objectMapper,
            metricsService,
            requireProperty(propertyName),
            tokenExchangeService,
        ) {
            tokenProvider.createMachineToMachineToken("api://$oppfolgingCluster.$oppfolgingNamespace.$oppfolgingAppNavn/.default")
        }
    }

    @Bean
    fun veilarbarenaClient(
        tokenProvider: AzureAdMachineToMachineTokenClient,
        metricsService: MetricsService
    ): VeilarbarenaClient {
        val baseUrl = requireProperty("VEILARBARENA_URL")
        val veilarbarenaCluster = requireProperty("VEILARBARENA_CLUSTER")
        val veilarbarenaTokenProvider = {
            try {
                tokenProvider.createMachineToMachineToken("api://$veilarbarenaCluster.pto.veilarbarena/.default")
            } catch (e: Exception) {
                "no token"
            }
        }
        return VeilarbarenaClient(baseUrl, metricsService, veilarbarenaTokenProvider)
    }

    @Bean
    fun oppfolgingGateway(
        oppfolgingClient: OppfolgingClient,
        veilarbarenaClient: VeilarbarenaClient
    ): OppfolgingGateway {
        return OppfolgingGatewayImpl(oppfolgingClient, veilarbarenaClient)
    }
}

val oppfolgingNamespace = requireProperty("VEILARBOPPFOLGINGAPI_NAMESPACE")
val oppfolgingAppNavn = "veilarboppfolging"
val oppfolgingCluster = requireProperty("VEILARBOPPFOLGINGAPI_CLUSTER")
val oppfolgingApi = DownstreamApi(oppfolgingCluster, oppfolgingNamespace, oppfolgingAppNavn)
