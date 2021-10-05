package no.nav.fo.veilarbregistrering.config

import io.micrometer.core.instrument.Tag
import no.nav.common.auth.Constants
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.Event
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import org.springframework.http.HttpHeaders
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AuthStatsFilter(private val prometheusMetricsService: PrometheusMetricsService): Filter {

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val request: HttpServletRequest = servletRequest as HttpServletRequest
        val cookieNames = request.cookies.map { it.name }
        val sts = request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer")
        when {
            Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME in cookieNames -> {
                log.info("Token type encountered: [AADB2C]")
                prometheusMetricsService.registrer(event, aadb2cTag)
            }
            Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME in cookieNames -> {
                log.info("Token type encountered: [AAD]")
                prometheusMetricsService.registrer(event, aadTag)
            }
            Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME in cookieNames -> {
                log.info("Token type encountered: [OPENAM]")
                prometheusMetricsService.registrer(event, openamTag)
            }
            sts -> {
                log.info("Token type encountered: [STS]")
                prometheusMetricsService.registrer(event, stsTag)
            }
        }
        chain.doFilter(servletRequest, servletResponse)
    }

    companion object {
        private const val tokenType = "tokenType"

        private val log = loggerFor<AuthStatsFilter>()
        private val event = Event.of("AUTH_TOKEN_TYPE")

        private val aadb2cTag = Tag.of(tokenType, "AADB2C")
        private val aadTag = Tag.of(tokenType, "AAD")
        private val openamTag = Tag.of(tokenType, "OPENAM")
        private val stsTag = Tag.of(tokenType, "STS")

    }
}