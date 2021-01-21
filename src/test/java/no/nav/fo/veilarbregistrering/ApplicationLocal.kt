package no.nav.fo.veilarbregistrering

import no.nav.fo.veilarbregistrering.config.ApplicationTestConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@EnableAutoConfiguration
@Import(ApplicationTestConfig::class)
open class ApplicationLocal

fun main(vararg args: String) {


    runApplication<ApplicationLocal>(*args) {
        setAdditionalProfiles("local")
    }

}