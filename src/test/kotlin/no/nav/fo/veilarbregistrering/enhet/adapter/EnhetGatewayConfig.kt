package no.nav.fo.veilarbregistrering.enhet.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EnhetGatewayConfig {
    @Bean
    fun enhetGateway(): EnhetGateway = mockk()
}
