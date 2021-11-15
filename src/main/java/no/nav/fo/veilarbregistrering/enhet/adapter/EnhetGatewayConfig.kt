package no.nav.fo.veilarbregistrering.enhet.adapter

import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EnhetGatewayConfig {
    @Bean
    fun enhetClient(): EnhetRestClient {
        return EnhetRestClient(requireProperty(ENHET_PROPERTY_NAME))
    }

    @Bean
    fun enhetGateway(enhetRestClient: EnhetRestClient?): EnhetGateway {
        return EnhetGatewayImpl(enhetRestClient!!)
    }

    companion object {
        private const val ENHET_PROPERTY_NAME = "ENHET_URL"
    }
}