package no.nav.fo.veilarbregistrering.bruker.krr

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
        val cluster = requireProperty(DIGDIR_KRR_CLUSTERT_PROPERTY_NAME)

        return DigDirKrrProxyClient(baseUrl) {
            serviceToServiceTokenProvider.getServiceToken("digdir-krr-proxy", "team-rocket", cluster)
        }
    }

    @Bean
    fun krrGateway(digdirKrrGateway: DigDirKrrProxyClient): KrrGateway {
        return KrrGatewayImpl(digdirKrrGateway)
    }

    companion object {
        private const val DIGDIR_KRR_PROXY_URL_PROPERTY_NAME = "DIGDIR_KRR_PROXY_BASE_URL"
        private const val DIGDIR_KRR_CLUSTERT_PROPERTY_NAME = "DIGDIR_KRR_CLUSTER"
    }
}