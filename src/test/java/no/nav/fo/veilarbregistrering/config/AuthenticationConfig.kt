package no.nav.fo.veilarbregistrering.config

import io.mockk.mockk
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.fo.veilarbregistrering.oauth2.AadOboService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthenticationConfig {

    @Bean
    fun authContextHolder(): AuthContextHolder {
        return AuthContextHolderThreadLocal.instance()
    }

    @Bean
    fun aadOboService(): AadOboService {
        System.setProperty("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", "changeme")
        System.setProperty("AZURE_APP_WELL_KNOWN_URL", "changeme")
        System.setProperty("AZURE_APP_CLIENT_ID", "changeme")
        System.setProperty("AZURE_APP_CLIENT_SECRET", "changeme")

        return AadOboService(mockk(relaxed = true))
    }
}