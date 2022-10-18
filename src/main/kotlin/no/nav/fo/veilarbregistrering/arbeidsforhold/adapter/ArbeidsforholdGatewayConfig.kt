package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.config.requireClusterName
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.tokenveksling.DownstreamApi
import no.nav.fo.veilarbregistrering.tokenveksling.TokenExchangeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ArbeidsforholdGatewayConfig {
    @Bean
    fun aaregRestClient(
        metricsService: MetricsService,
        machineToMachineTokenClient: AzureAdMachineToMachineTokenClient,
        tokenExchangeService: TokenExchangeService
    ): AaregRestClient {
        return AaregRestClient(
            metricsService,
            requireProperty(REST_URL),
            tokenExchangeService
        )
    }

    @Bean
    fun arbeidsforholdGateway(aaregRestClient: AaregRestClient): ArbeidsforholdGateway {
        return ArbeidsforholdGatewayImpl(aaregRestClient)
    }

    companion object {
        private const val REST_URL = "AAREG_REST_API"
    }
}

val aaregApi = DownstreamApi(requireProperty("AAREG_CLUSTER"), "arbeidsforhold", requireProperty("AAREG_APPNAME"))