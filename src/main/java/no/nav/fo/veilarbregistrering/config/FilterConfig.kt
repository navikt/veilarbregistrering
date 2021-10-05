package no.nav.fo.veilarbregistrering.config

import com.nimbusds.oauth2.sdk.token.AccessTokenType.BEARER
import no.nav.common.auth.Constants
import no.nav.common.auth.context.UserRole
import no.nav.common.auth.oidc.filter.OidcAuthenticator
import no.nav.common.auth.oidc.filter.OidcAuthenticatorConfig
import no.nav.common.log.LogFilter
import no.nav.common.rest.filter.SetStandardHttpHeadersFilter
import no.nav.common.utils.EnvironmentUtils
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.AUTHORIZATION
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Configuration
class FilterConfig {

    private fun createOpenAmAuthenticatorConfig(): OidcAuthenticatorConfig? {
        val discoveryUrl = EnvironmentUtils.getRequiredProperty("OPENAM_DISCOVERY_URL")
        val clientId = EnvironmentUtils.getRequiredProperty("VEILARBLOGIN_OPENAM_CLIENT_ID")
        val refreshUrl = EnvironmentUtils.getRequiredProperty("VEILARBLOGIN_OPENAM_REFRESH_URL")
        return OidcAuthenticatorConfig()
            .withDiscoveryUrl(discoveryUrl)
            .withClientId(clientId)
            .withRefreshUrl(refreshUrl)
            .withRefreshTokenCookieName(Constants.REFRESH_TOKEN_COOKIE_NAME)
            .withIdTokenCookieName(Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME) //FIXME: Verifiser riktig bruk
            .withUserRole(UserRole.INTERN)
    }

    private fun createVeilarbloginAADConfig(): OidcAuthenticatorConfig? {
        val discoveryUrl = EnvironmentUtils.getRequiredProperty("AAD_DISCOVERY_URL")
        val clientId = EnvironmentUtils.getRequiredProperty("VEILARBLOGIN_AAD_CLIENT_ID")
        return OidcAuthenticatorConfig()
            .withDiscoveryUrl(discoveryUrl)
            .withClientId(clientId)
            .withIdTokenCookieName(Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME)
            .withUserRole(UserRole.INTERN)
    }

    private fun createAzureAdB2CConfig(): OidcAuthenticatorConfig? {
        val discoveryUrl = EnvironmentUtils.getRequiredProperty("LOGINSERVICE_IDPORTEN_DISCOVERY_URL")
        val clientId = EnvironmentUtils.getRequiredProperty("LOGINSERVICE_IDPORTEN_AUDIENCE")
        return OidcAuthenticatorConfig()
            .withDiscoveryUrl(discoveryUrl)
            .withClientId(clientId)
            .withIdTokenCookieName(Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME)
            .withUserRole(UserRole.EKSTERN)
    }

    private fun createSystemUserAuthenticatorConfig(): OidcAuthenticatorConfig? {
        val discoveryUrl = EnvironmentUtils.getRequiredProperty("SECURITY_TOKEN_SERVICE_DISCOVERY_URL")
        val clientId = EnvironmentUtils.getRequiredProperty("SECURITY_TOKEN_SERVICE_CLIENT_ID")
        return OidcAuthenticatorConfig()
            .withDiscoveryUrl(discoveryUrl)
            .withClientId(clientId)
            .withUserRole(UserRole.SYSTEM)
    }

    @Bean
    fun pingFilter(): FilterRegistrationBean<*>? {
        // Veilarbproxy trenger dette endepunktet for å sjekke at tjenesten lever
        // /internal kan ikke brukes siden det blir stoppet før det kommer frem
        val registration = FilterRegistrationBean<PingFilter>()
        registration.filter = PingFilter()
        registration.order = 1
        registration.addUrlPatterns("/api/ping")
        return registration
    }

    @Bean
    fun loginStatsFilter(): FilterRegistrationBean<*> {
        return FilterRegistrationBean<Filter>().apply {
            filter = AuthStatsFilter()
            order = 2
            addUrlPatterns("/*")
        }
    }

    @Bean
    open fun authenticationFilterRegistrationBean(): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<OidcAuthenticationFilterMigreringBypass>()
        val authenticationFilter = OidcAuthenticationFilterMigreringBypass(
                OidcAuthenticator.fromConfigs(
                        createOpenAmAuthenticatorConfig(),
                        createVeilarbloginAADConfig(),
                        createAzureAdB2CConfig(),
                        createSystemUserAuthenticatorConfig(),
                )
        )
        registration.setFilter(authenticationFilter)
        registration.order = 4
        registration.addUrlPatterns("/api/*")
        return registration
    }

    @Bean
    open fun logFilterRegistrationBean(): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<LogFilter>()
        registration.setFilter(
                LogFilter(
                        EnvironmentUtils.requireApplicationName(),
                        EnvironmentUtils.isDevelopment().orElse(false)
                )
        )
        registration.order = 3
        registration.addUrlPatterns("/*")
        return registration
    }

    @Bean
    open fun setStandardHeadersFilterRegistrationBean(): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<SetStandardHttpHeadersFilter>()
        registration.setFilter(SetStandardHttpHeadersFilter())
        registration.order = 5
        registration.addUrlPatterns("/*")
        return registration
    }
}