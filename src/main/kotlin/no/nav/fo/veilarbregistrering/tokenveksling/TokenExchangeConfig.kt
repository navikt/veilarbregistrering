package no.nav.fo.veilarbregistrering.tokenveksling

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
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

    @Bean
    fun azureAdMachineToMachineTokenClient(): AzureAdMachineToMachineTokenClient {
        return AzureAdTokenClientBuilder.builder()
            .withNaisDefaults()
            .buildMachineToMachineTokenClient()
    }
}