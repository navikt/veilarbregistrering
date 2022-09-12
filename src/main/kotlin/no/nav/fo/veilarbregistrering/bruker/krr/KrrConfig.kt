package no.nav.fo.veilarbregistrering.bruker.krr

import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.bruker.KrrGateway
import no.nav.fo.veilarbregistrering.config.requireProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KrrConfig {

    @Bean
    fun digDirKrrProxyClient(machineToMachineTokenClient: AzureAdMachineToMachineTokenClient): DigDirKrrProxyClient {
        val baseUrl = requireProperty(DIGDIR_KRR_PROXY_URL_PROPERTY_NAME)
        val cluster = requireProperty(DIGDIR_KRR_CLUSTER_PROPERTY_NAME)

        return DigDirKrrProxyClient(baseUrl) {
            machineToMachineTokenClient.createMachineToMachineToken("api://$cluster.team-rocket.digdir-krr-proxy/.default")
        }
    }

    @Bean
    fun krrGateway(digdirKrrGateway: DigDirKrrProxyClient): KrrGateway {
        return KrrGatewayImpl(digdirKrrGateway)
    }

    companion object {
        private const val DIGDIR_KRR_PROXY_URL_PROPERTY_NAME = "DIGDIR_KRR_PROXY_BASE_URL"
        private const val DIGDIR_KRR_CLUSTER_PROPERTY_NAME = "DIGDIR_KRR_CLUSTER"
    }
}