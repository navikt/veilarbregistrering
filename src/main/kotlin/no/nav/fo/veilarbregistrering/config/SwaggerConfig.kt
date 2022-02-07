package no.nav.fo.veilarbregistrering.config

import io.swagger.v3.oas.models.OpenAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class SwaggerConfig {
    //  Path to Swagger UI: /veilarbregistrering/swagger-ui.html

    @Bean
    fun swaggerApi() = OpenAPI()
}
