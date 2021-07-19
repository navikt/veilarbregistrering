package no.nav.fo.veilarbregistrering.bruker.krr

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.KrrGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KrrConfig {

    @Bean
    fun krrGateway(): KrrGateway = mockk()
}
