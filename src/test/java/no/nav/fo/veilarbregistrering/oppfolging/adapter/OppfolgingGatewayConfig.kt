package no.nav.fo.veilarbregistrering.oppfolging.adapter

import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppfolgingGatewayConfig {
    @Bean
    fun oppfolgingClient(): OppfolgingClient = OppfolgingClientMock()

    @Bean
    fun oppfolgingGateway(oppfolgingClient: OppfolgingClient?): OppfolgingGateway =
        OppfolgingGatewayImpl(oppfolgingClient)
}