package no.nav.fo.veilarbregistrering.autentisering.tokenveksling

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.config.requireProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TokenExchangeConfig {

    @Bean
    fun tokenResolver(authContextHolder: AuthContextHolder): TokenResolver {
        return TokenResolver(authContextHolder, TokenIssuers())
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

class TokenIssuers(
    val tokenXIssuer: String = requireProperty("TOKEN_X_ISSUER"),
    val aadIssuer: String = requireProperty("AZURE_OPENID_CONFIG_ISSUER"),
    val idportenIssuer: String = requireProperty("IDPORTEN_ISSUER"),
)