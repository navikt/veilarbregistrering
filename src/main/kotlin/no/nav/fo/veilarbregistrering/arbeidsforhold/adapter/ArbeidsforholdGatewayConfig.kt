package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.config.requireClusterName
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ArbeidsforholdGatewayConfig {
    @Bean
    fun aaregRestClient(
        metricsService: MetricsService,
        systemUserTokenProvider: SystemUserTokenProvider,
        authContextHolder: AuthContextHolder,
        serviceToServiceTokenProvider: ServiceToServiceTokenProvider
    ): AaregRestClient {
        val aaregCluster = requireClusterName()
        return AaregRestClient(
            metricsService,
            requireProperty(REST_URL),
            systemUserTokenProvider,
            authContextHolder
        ) {
            try {
                val serviceName = if (isDevelopment()) "aareg-services-nais-q1" else "aareg-services-nais"
                serviceToServiceTokenProvider.getServiceToken(serviceName, "arbeidsforhold", aaregCluster)
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
    }
}