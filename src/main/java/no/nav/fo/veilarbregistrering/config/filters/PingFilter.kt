package no.nav.fo.veilarbregistrering.config.filters

import org.springframework.http.HttpStatus
import javax.servlet.*
import javax.servlet.FilterConfig
import javax.servlet.http.HttpServletResponse

class PingFilter : Filter {
    override fun init(filterConfig: FilterConfig) {}
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val httpServletResponse = servletResponse as HttpServletResponse
        httpServletResponse.status = HttpStatus.OK.value()
    }

    override fun destroy() {}
}