package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PdlOppslagConfig {
    @Bean
    fun pdlOppslagClient() = PdlOppslagClient("http://localhost/pdl") { "token" }

    @Bean
    fun pdlOppslagGateway(pdlOppslagClient: PdlOppslagClient): PdlOppslagGateway = PdlOppslagGatewayImpl(pdlOppslagClient)
}
