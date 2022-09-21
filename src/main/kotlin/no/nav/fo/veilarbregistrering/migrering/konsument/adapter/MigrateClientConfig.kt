package no.nav.fo.veilarbregistrering.migrering.konsument.adapter

import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MigrateClientConfig {

    @Bean
    fun migrateClient(tokenProvider: AzureAdMachineToMachineTokenClient): MigrateClient {
        return if (isOnPrem()) {
            OnPremMigrationClient()
        } else {
            val proxyTokenProvider = {
                val pawProxyCluster = requireProperty("PAW_PROXY_CLUSTER")
                tokenProvider.createMachineToMachineToken("api://$pawProxyCluster.paw.paw-proxy/.default")
            }

            val baseUrl = System.getenv("VEILARBREGISTRERING_ONPREM_URL")!!
            GcpMigrateClient(baseUrl, proxyTokenProvider)
        }
    }
}