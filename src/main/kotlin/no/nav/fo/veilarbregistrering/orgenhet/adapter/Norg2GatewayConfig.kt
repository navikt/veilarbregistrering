package no.nav.fo.veilarbregistrering.orgenhet.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Norg2GatewayConfig {

    @Bean
    fun norgRestClient(objectMapper: ObjectMapper): Norg2RestClient {
        return Norg2RestClient(requireProperty(NORG2_PROPERTY_NAME), objectMapper)
    }

    @Bean
    fun norgGateway(norg2RestClient: Norg2RestClient): Norg2Gateway {
        return Norg2GatewayImpl(norg2RestClient)
    }

    companion object {
        private const val NORG2_PROPERTY_NAME = "NORG2_URL"
    }
}