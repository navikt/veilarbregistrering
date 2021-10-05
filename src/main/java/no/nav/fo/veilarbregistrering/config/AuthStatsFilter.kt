package no.nav.fo.veilarbregistrering.config

import io.micrometer.core.instrument.Tag
import no.nav.common.auth.Constants
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.Event
import org.springframework.http.HttpHeaders
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AuthStatsFilter : Filter {

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val request: HttpServletRequest = servletRequest as HttpServletRequest
        val cookieNames = request.cookies.map { it.name }
        val sts = request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer")
        when {
            Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME in cookieNames -> log.info("Token type encountered: [AADB2C]")
            Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME in cookieNames -> log.info("Token type encountered: [AAD]")
            Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME in cookieNames -> log.info("Token type encountered: [OPENAM]")
            sts -> log.info("Token type encountered: [STS]")
        }
        chain.doFilter(servletRequest, servletResponse)
    }

    companion object {
        private val log = loggerFor<AuthStatsFilter>()
    }
}