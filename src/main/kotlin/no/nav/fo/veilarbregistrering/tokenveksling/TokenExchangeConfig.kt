package no.nav.fo.veilarbregistrering.tokenveksling

import no.nav.common.auth.context.AuthContextHolder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TokenExchangeConfig {

    @Bean
    fun tokenResolver(authContextHolder: AuthContextHolder): TokenResolver {
        return TokenResolver(authContextHolder)
    }

    @Bean
    fun tokenExchangeService(tokenResolver: TokenResolver): TokenExchangeService {
        return TokenExchangeService(tokenResolver)
    }
}