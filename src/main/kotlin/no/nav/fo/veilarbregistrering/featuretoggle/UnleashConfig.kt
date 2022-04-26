package no.nav.fo.veilarbregistrering.featuretoggle

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.featuretoggle.UnleashClientImpl
import no.nav.fo.veilarbregistrering.config.requireApplicationName
import no.nav.fo.veilarbregistrering.config.requireProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UnleashConfig {

    @Bean
    fun unleashClient(authContextHolder: AuthContextHolder): UnleashClient {
        return UnleashClientImpl(
            requireProperty(UNLEASH_API_URL_PROPERTY),
            requireApplicationName(),
            listOf(ByUserIdStrategy(authContextHolder))
        )
    }

    companion object {
        const val UNLEASH_API_URL_PROPERTY = "UNLEASH_API_URL"
    }
}