package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.config.requireClusterName
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ArbeidsforholdGatewayConfig {
    @Bean
    fun aaregRestClient(
        unleashClient: UnleashClient,
        prometheusMetricsService: PrometheusMetricsService,
        systemUserTokenProvider: SystemUserTokenProvider,
        authContextHolder: AuthContextHolder,
        serviceToServiceTokenProvider: ServiceToServiceTokenProvider
    ): AaregRestClient {
        val aaregCluster = requireClusterName()
        return AaregRestClient(
            unleashClient,
            prometheusMetricsService,
            requireProperty(REST_URL),
            requireProperty(REST_URL_OLD),
            systemUserTokenProvider,
            authContextHolder
        ) {
            try {
                serviceToServiceTokenProvider.getServiceToken("aareg-services-nais-q1", "arbeidsforhold", aaregCluster)
            } catch (e: Exception) {
                logger.warn("Henting av token for aad-kall til aareg feilet: ", e)
                "no token"
            }
        }
    }

    @Bean
    fun arbeidsforholdGateway(aaregRestClient: AaregRestClient): ArbeidsforholdGateway {
        return ArbeidsforholdGatewayImpl(aaregRestClient)
    }

    companion object {
        private const val REST_URL = "AAREG_REST_API"
        private const val REST_URL_OLD = "AAREG_REST_API_OLD"
    }
}