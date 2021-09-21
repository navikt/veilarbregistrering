package no.nav.fo.veilarbregistrering.config

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.featuretoggle.UnleashClientImpl
import no.nav.common.utils.EnvironmentUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UnleashConfig {
    @Bean
    fun unleashClient(): UnleashClient {
        return UnleashClientImpl(
            EnvironmentUtils.getRequiredProperty(UNLEASH_API_URL_PROPERTY),
            EnvironmentUtils.requireApplicationName()
        )
    }

    companion object {
        const val UNLEASH_API_URL_PROPERTY = "UNLEASH_API_URL"
    }
}