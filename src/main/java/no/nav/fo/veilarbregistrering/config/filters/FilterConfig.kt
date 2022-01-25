package no.nav.fo.veilarbregistrering.config.filters

import no.nav.common.auth.Constants
import no.nav.common.auth.context.UserRole
import no.nav.common.auth.oidc.filter.AzureAdUserRoleResolver
import no.nav.common.auth.oidc.filter.OidcAuthenticator
import no.nav.common.auth.oidc.filter.OidcAuthenticatorConfig
import no.nav.common.log.LogFilter
import no.nav.common.rest.filter.SetStandardHttpHeadersFilter
import no.nav.fo.veilarbregistrering.config.*
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.servlet.Filter

@Configuration
class FilterConfig {

    private fun createOpenAmAuthenticatorConfig(): OidcAuthenticatorConfig? {
        val discoveryUrl = requireProperty("OPENAM_DISCOVERY_URL")
        val clientId = requireProperty("VEILARBLOGIN_OPENAM_CLIENT_ID")
        val refreshUrl = requireProperty("VEILARBLOGIN_OPENAM_REFRESH_URL")
        return OidcAuthenticatorConfig()
            .withDiscoveryUrl(discoveryUrl)
            .withClientId(clientId)
            .withRefreshUrl(refreshUrl)
            .withRefreshTokenCookieName(Constants.REFRESH_TOKEN_COOKIE_NAME)
            .withIdTokenCookieName(Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME) //FIXME: Verifiser riktig bruk
            .withUserRole(UserRole.INTERN)
    }

    private fun createVeilarbloginAADConfig(): OidcAuthenticatorConfig? {
        val discoveryUrl = requireProperty("AAD_DISCOVERY_URL")
        val clientId = requireProperty("VEILARBLOGIN_AAD_CLIENT_ID")
        return OidcAuthenticatorConfig()
            .withDiscoveryUrl(discoveryUrl)
            .withClientId(clientId)
            .withIdTokenCookieName(Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME)
            .withUserRole(UserRole.INTERN)
    }

    /*
    * 24.01.2022 : This config should work for aad tokens obtained with both
    * client-credentials- and on-behalf-of flow
    */
    private fun createAadTokenConfig(): OidcAuthenticatorConfig? {
        val discoveryUrl = requireProperty("AZURE_APP_WELL_KNOWN_URL")
        val allowedAudience =
            requireProperty("AZURE_APP_CLIENT_ID")

        return OidcAuthenticatorConfig()
            .withDiscoveryUrl(discoveryUrl)
            .withClientId(allowedAudience)
            .withUserRoleResolver(AzureAdUserRoleResolver())
    }

    private fun createAzureAdB2CConfig(): OidcAuthenticatorConfig? {
        val discoveryUrl = requireProperty("LOGINSERVICE_IDPORTEN_DISCOVERY_URL")
        val clientId = requireProperty("LOGINSERVICE_IDPORTEN_AUDIENCE")
        return OidcAuthenticatorConfig()
            .withDiscoveryUrl(discoveryUrl)
            .withClientId(clientId)
            .withIdTokenCookieName(Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME)
            .withUserRole(UserRole.EKSTERN)
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
            order = 3
            addUrlPatterns("/*")
        }
    }

    @Bean
    fun authenticationFilterRegistrationBean(): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<OidcAuthenticationFilterMigreringBypass>()
        val authenticationFilter = OidcAuthenticationFilterMigreringBypass(
            OidcAuthenticator.fromConfigs(
                createOpenAmAuthenticatorConfig(),
                createVeilarbloginAADConfig(),
                createAzureAdB2CConfig(),
                createAadTokenConfig(),
            )
        )
        registration.filter = authenticationFilter
        registration.order = 4
        registration.addUrlPatterns("/api/*")
        return registration
    }

    @Bean
    fun logFilterRegistrationBean(): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<LogFilter>()
        registration.filter = LogFilter(
            requireApplicationName(),
            isDevelopment()
        )
        registration.order = 2
        registration.addUrlPatterns("/*")
        return registration
    }



    @Bean
    fun setStandardHeadersFilterRegistrationBean(): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<SetStandardHttpHeadersFilter>()
        registration.filter = SetStandardHttpHeadersFilter()
        registration.order = 5
        registration.addUrlPatterns("/*")
        return registration
    }
}