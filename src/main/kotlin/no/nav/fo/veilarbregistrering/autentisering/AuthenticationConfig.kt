package no.nav.fo.veilarbregistrering.autentisering

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthenticationConfig {

    @Bean
    fun authContextHolder(): AuthContextHolder {
        return AuthContextHolderThreadLocal.instance()
    }
}