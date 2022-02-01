package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ArbeidsforholdGatewayConfig {
    @Bean
    fun aaregRestClient(
        systemUserTokenProvider: SystemUserTokenProvider,
        authContextHolder: AuthContextHolder
    ): AaregRestClient {
        return AaregRestClient(
            requireProperty(REST_URL),
            requireProperty(REST_URL_OLD),
            systemUserTokenProvider,
            authContextHolder
        ) { "no token yet" }
    }

    @Bean
    fun arbeidsforholdGateway(aaregRestClient: AaregRestClient): ArbeidsforholdGateway {
        return ArbeidsforholdGatewayImpl(aaregRestClient)
    }

    companion object {
        private const val REST_URL = "AAREG_REST_API"
        private const val REST_URL_OLD = "AAREG_REST_API_OLD"
    }
}