package no.nav.fo.veilarbregistrering.config.filters

import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import io.micrometer.core.instrument.Tag
import no.nav.common.auth.Constants
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import java.text.ParseException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AuthStatsFilter(private val metricsService: MetricsService) : Filter {

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val request: HttpServletRequest = servletRequest as HttpServletRequest
        val consumerId = getConsumerId(request)

        val cookieNames = request.cookies?.map { it.name } ?: emptyList()
        val headerValue = request.getHeader(HttpHeaders.AUTHORIZATION)        
        val bearerToken = headerValue?.substring("Bearer ".length)
        val type = when {
            Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME in cookieNames -> "ID-PORTEN"
            Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME in cookieNames -> "AAD"
            !bearerToken.isNullOrBlank() -> checkBearerTokenForType(bearerToken)
            else -> null
        }

        try {
            type?.let {
                MDC.put(TOKEN_TYPE, type)
                metricsService.registrer(Events.REGISTRERING_TOKEN, Tag.of("type", type))
                log.info("Authentication with: [$it] request path: [${request.servletPath}] consumer: [$consumerId]")
            }
            chain.doFilter(servletRequest, servletResponse)
        } finally {
            MDC.remove(TOKEN_TYPE)
        }
    }

    private fun checkBearerTokenForType(token: String): String =
        try {
            val jwt = JWTParser.parse(token)
            when {
                jwt.erAzureAdToken() -> "AAD"
                jwt.erIdPortenToken() -> "ID-PORTEN"
                jwt.erTokenXToken() -> "TOKENX"
                else -> "STS"
            }
        } catch (e: ParseException) {
            log.warn("Couldnt parse token $token")
            when {
                token.contains("microsoftonline.com") -> "AAD"
                token.contains("difi.no") -> "ID-PORTEN"
                token.contains("tokendings") -> "TOKENX"
                else -> "STS"
            }
        }

    companion object {
        private const val TOKEN_TYPE = "tokenType"
        private val log = loggerFor<AuthStatsFilter>()

        private fun getConsumerId(request: HttpServletRequest) = request.getHeader("Nav-Consumer-Id")
    }
}

fun JWT.erAzureAdToken(): Boolean = this.jwtClaimsSet.issuer.contains("microsoftonline.com")
fun JWT.erIdPortenToken(): Boolean = this.jwtClaimsSet.issuer.contains("difi.no")
fun JWT.erTokenXToken(): Boolean = this.jwtClaimsSet.issuer.contains("tokendings")