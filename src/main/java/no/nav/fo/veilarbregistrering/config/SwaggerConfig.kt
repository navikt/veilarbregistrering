package no.nav.fo.veilarbregistrering.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
open class SwaggerConfig {
    //  Path to Swagger UI: /veilarbregistrering/swagger-ui.html
    @Bean
    open fun docket(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis { handler ->
                if (handler == null) return@apis false
                handler.key().pathMappings.stream().anyMatch { path -> path.startsWith("/api") }
            }
            .build()
    }
}