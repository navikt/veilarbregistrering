package no.nav.fo.veilarbregistrering

import no.nav.fo.veilarbregistrering.config.ApplicationTestConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@EnableAutoConfiguration
@Import(ApplicationTestConfig::class)
open class TestApplication

fun main(vararg args: String) {

    // We need to initialize the driver before spring starts or Flyway will not be able to use the driver
/*        LocalH2Database.setUsePersistentDb();
    TestDriver.init();*/

    runApplication<TestApplication>(*args) {
        setAdditionalProfiles("local")
    }

}