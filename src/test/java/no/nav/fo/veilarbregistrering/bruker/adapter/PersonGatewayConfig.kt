package no.nav.fo.veilarbregistrering.bruker.adapter

import no.nav.fo.veilarbregistrering.bruker.PersonGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class PersonGatewayConfig {
    @Bean
    fun personGateway(): PersonGateway {
        return PersonGateway { Optional.empty() }
    }
}
