package no.nav.fo.veilarbregistrering.bruker.krr

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.fo.veilarbregistrering.bruker.KrrGateway
import no.nav.fo.veilarbregistrering.config.requireProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KrrConfig {

    @Bean
    fun digDirKrrProxyClient(serviceToServiceTokenProvider: ServiceToServiceTokenProvider): DigDirKrrProxyClient {
        val baseUrl = requireProperty(DIGDIR_KRR_PROXY_URL_PROPERTY_NAME)
        val cluster = requireProperty(KRR_CLUSTER_PROPERTY_NAME)

        return DigDirKrrProxyClient(baseUrl) {
            serviceToServiceTokenProvider.getServiceToken("digdir-krr-proxy", "team-rocket", cluster)
        }
    }

    @Bean
    fun krrClient(serviceToServiceTokenProvider: ServiceToServiceTokenProvider): KrrClient {
        val baseUrl = requireProperty(KRR_URL_PROPERTY_NAME)
        val cluster = requireProperty(KRR_CLUSTER_PROPERTY_NAME)

        return KrrClient(baseUrl) {
            serviceToServiceTokenProvider.getServiceToken("dkif", "team-rocket", cluster)
        }
    }

    @Bean
    fun krrGateway(krrClient: KrrClient, digdirKrrGateway: DigDirKrrProxyClient, unleashClient: UnleashClient): KrrGateway {
        return KrrGatewayImpl(krrClient, digdirKrrGateway, unleashClient)
    }

    companion object {
        private const val DIGDIR_KRR_PROXY_URL_PROPERTY_NAME = "DIGDIR_KRR_PROXY_BASE_URL"
        private const val KRR_URL_PROPERTY_NAME = "KRR_BASE_URL"
        private const val KRR_CLUSTER_PROPERTY_NAME = "KRR_CLUSTER"
    }
}