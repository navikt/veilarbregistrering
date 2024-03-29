package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.autentisering.tokenveksling.DownstreamApi
import no.nav.fo.veilarbregistrering.autentisering.tokenveksling.TokenExchangeService
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.MetricsService
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