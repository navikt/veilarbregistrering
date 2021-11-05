package no.nav.fo.veilarbregistrering.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.fo.veilarbregistrering.config.DateConfiguration.dateModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper(): ObjectMapper = objectMapper

}

val objectMapper: ObjectMapper = jacksonObjectMapper()
    .findAndRegisterModules()
    .registerModule(KotlinModule())
    .registerModule(dateModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)