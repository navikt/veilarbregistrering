package no.nav.fo.veilarbregistrering.oppfolging.adapter

import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.VeilarbarenaClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppfolgingGatewayConfig {

    @Bean
    fun oppfolgingClient(): OppfolgingClient = OppfolgingClientMock()

    @Bean
    fun veilarbarenaClient(): VeilarbarenaClient = mockk(relaxed = true)

    @Bean
    fun oppfolgingGateway(oppfolgingClient: OppfolgingClient, veilarbarenaClient: VeilarbarenaClient, unleashClient: UnleashClient): OppfolgingGateway =
        OppfolgingGatewayImpl(oppfolgingClient, veilarbarenaClient, unleashClient, mockk(relaxed = true))
}