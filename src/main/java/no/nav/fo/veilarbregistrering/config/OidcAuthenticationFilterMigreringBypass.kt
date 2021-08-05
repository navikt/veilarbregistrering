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
        if (!AntPathMatcher().match(MIGRERING_PATTERN, servletPath)) {
            log.info("Gjør autentisering for request til [${servletPath}")
            super.doFilter(servletRequest, servletResponse, chain)
        } else {
            log.info("OMGÅR autentisering for request til [${servletPath}")
            chain.doFilter(servletRequest, servletResponse)
        }
    }

    companion object {
        const val MIGRERING_PATTERN = "/api/migrering"
        private val log = loggerFor<OidcAuthenticationFilterMigreringBypass>()
    }
}