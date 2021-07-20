package no.nav.fo.veilarbregistrering.orgenhet.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Norg2GatewayConfig {

    @Bean
    fun norgGateway(): Norg2Gateway = mockk()
}
