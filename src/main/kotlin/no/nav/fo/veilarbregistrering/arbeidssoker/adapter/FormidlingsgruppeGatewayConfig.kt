package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FormidlingsgruppeGatewayConfig {
    @Bean
    fun arenaOrdsTokenProviderClient(): ArenaOrdsTokenProviderClient =
        ArenaOrdsTokenProviderClient(requireProperty(ARENA_ORDS_TOKEN_PROVIDER))

    @Bean
    fun formidlingsgruppeRestClient(
        arenaOrdsTokenProviderClient: ArenaOrdsTokenProviderClient,
        azureAdMachineToMachineTokenProvider: AzureAdMachineToMachineTokenClient,
        metricsService: MetricsService
    ): FormidlingsgruppeRestClient {
        val proxyTokenProvider = {
            val pawProxyCluster = requireProperty("PAW_PROXY_CLUSTER")
            azureAdMachineToMachineTokenProvider.createMachineToMachineToken("api://$pawProxyCluster.paw.paw-proxy/.default")
        }
        val arenaTokenProvider = { arenaOrdsTokenProviderClient.token }
        return FormidlingsgruppeRestClient(
            requireProperty(ARENA_ORDS_API),
            metricsService,
            arenaTokenProvider,
            proxyTokenProvider
        )
    }

    @Bean
    fun formidlingsgruppeGateway(
        formidlingsgruppeRestClient: FormidlingsgruppeRestClient
    ): FormidlingsgruppeGateway {
        return FormidlingsgruppeGatewayImpl(formidlingsgruppeRestClient)
    }

    companion object {
        private const val ARENA_ORDS_TOKEN_PROVIDER = "ARENA_ORDS_TOKEN_PROVIDER"
        private const val ARENA_ORDS_API = "ARENA_ORDS_API"
    }
}