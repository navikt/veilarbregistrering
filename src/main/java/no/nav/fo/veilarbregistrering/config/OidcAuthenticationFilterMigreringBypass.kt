package no.nav.fo.veilarbregistrering.config

import no.nav.common.auth.oidc.filter.OidcAuthenticationFilter
import no.nav.common.auth.oidc.filter.OidcAuthenticator
import no.nav.fo.veilarbregistrering.log.loggerFor
import org.springframework.util.AntPathMatcher
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class OidcAuthenticationFilterMigreringBypass(oidcAuthenticators: List<OidcAuthenticator>) : OidcAuthenticationFilter(oidcAuthenticators) {
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val servletPath = (servletRequest as HttpServletRequest).servletPath
        if (mathcerMigrering(servletPath)) {
            log.info("Omgår autentisering for request til [${servletPath}")
            chain.doFilter(servletRequest, servletResponse)
        } else {
            super.doFilter(servletRequest, servletResponse, chain)
        }
    }

    companion object {
        fun mathcerMigrering(servletPath: String) = MIGRERING_PATTERNS.any { pathMatcher.match(it, servletPath) }

        private val pathMatcher = AntPathMatcher()
        private val MIGRERING_PATTERNS = listOf("/api/migrering", "/api/migrering/*")
        private val log = loggerFor<OidcAuthenticationFilterMigreringBypass>()
    }
}