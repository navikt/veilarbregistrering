package no.nav.fo.veilarbregistrering.config

import no.nav.common.auth.context.UserRole
import no.nav.common.test.auth.TestAuthContextFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FilterConfig {

    @Bean
    fun testSubjectFilterRegistrationBean(): FilterRegistrationBean<*>? {
        val registration = FilterRegistrationBean<TestAuthContextFilter>()
        registration.filter = TestAuthContextFilter(UserRole.INTERN, "randomUID")
        registration.order = 1
        registration.addUrlPatterns("/api/*")
        return registration
    }
}

