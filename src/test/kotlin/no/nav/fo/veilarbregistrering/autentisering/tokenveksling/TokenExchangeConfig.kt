package no.nav.fo.veilarbregistrering.autentisering.tokenveksling

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.autentisering.tokenveksling.TokenExchangeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TokenExchangeConfig {

    @Bean
    fun tokenExchangeService(): TokenExchangeService {
        return mockk(relaxed = true)
    }
}