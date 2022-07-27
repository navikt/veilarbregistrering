package no.nav.fo.veilarbregistrering.tokenveksling

import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TokenExchangeConfig {

    @Bean
    fun tokenExchangeService(): TokenExchangeService {
        return mockk(relaxed = true)
    }
}