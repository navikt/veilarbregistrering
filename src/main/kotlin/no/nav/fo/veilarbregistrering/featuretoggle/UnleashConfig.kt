package no.nav.fo.veilarbregistrering.featuretoggle

import io.getunleash.DefaultUnleash
import io.getunleash.Unleash
import io.getunleash.util.UnleashConfig
import no.nav.fo.veilarbregistrering.config.requireProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UnleashConfig {

    @Bean
    fun unleashClient(): Unleash {
        val config = UnleashConfig.builder()
            .appName(APP_NAME)
            .instanceId(APP_NAME)
            .unleashAPI(requireProperty(UNLEASH_SERVER_API_URL))
            .apiKey(requireProperty(UNLEASH_SERVER_API_TOKEN))
            .build()

        return DefaultUnleash(config)
    }

    companion object {
        const val UNLEASH_SERVER_API_URL = "UNLEASH_SERVER_API_URL"
        const val UNLEASH_SERVER_API_TOKEN = "UNLEASH_SERVER_API_TOKEN"
        const val APP_NAME = "veilarbregistrering"
    }
}
