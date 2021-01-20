package no.nav.fo.veilarbregistrering.config

import io.mockk.mockk
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FilterConfig {

    @Bean
    fun authenticationFilterRegistrationBean(): FilterRegistrationBean<*> = mockk()

    @Bean
    fun logFilterRegistrationBean(): FilterRegistrationBean<*> = mockk(relaxed = true)

    @Bean
    fun setStandardHeadersFilterRegistrationBean(): FilterRegistrationBean<*> = mockk()
}

