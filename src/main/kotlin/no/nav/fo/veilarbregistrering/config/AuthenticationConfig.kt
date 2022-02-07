package no.nav.fo.veilarbregistrering.config

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
    fun aadOboService(authContextHolder: AuthContextHolder): AadOboService {
        return AadOboService(authContextHolder)
    }
}