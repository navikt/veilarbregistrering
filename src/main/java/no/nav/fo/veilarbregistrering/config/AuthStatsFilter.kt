package no.nav.fo.veilarbregistrering.config

import no.nav.common.auth.Constants
import no.nav.fo.veilarbregistrering.log.loggerFor
import org.springframework.http.HttpHeaders
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AuthStatsFilter : Filter {

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val request: HttpServletRequest = servletRequest as HttpServletRequest
        val cookieNames = request.cookies?.map { it.name } ?: emptyList()
        val sts = request.getHeader(HttpHeaders.AUTHORIZATION)?.startsWith("Bearer") ?: false
        when {
            Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME in cookieNames -> log.info("Authentication with: [AADB2C]")
            Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME in cookieNames -> log.info("Authentication with: [AAD]")
            Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME in cookieNames -> log.info("Authentication with: [OPENAM]")
            sts -> log.info("Authentication with: [STS]")
        }
        chain.doFilter(servletRequest, servletResponse)
    }

    companion object {
        private val log = loggerFor<AuthStatsFilter>()
    }
}