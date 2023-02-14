package no.nav.fo.veilarbregistrering

import no.nav.common.utils.SslUtils
import no.nav.fo.veilarbregistrering.config.ApplicationConfig
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.config.db.DatabaseConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

fun main(vararg args: String) {
    Application.readSecrets()
    SslUtils.setupTruststore()
    runApplication<Application>(*args)
}

@EnableAutoConfiguration
@Import(ApplicationConfig::class)
class Application {

    companion object {
        internal fun readSecrets() {
            System.setProperty(
                DatabaseConfig.VEILARBREGISTRERINGDB_USERNAME,
                requireProperty("PAWVEILARBREGISTRERING_USERNAME")
            )
            System.setProperty(
                DatabaseConfig.VEILARBREGISTRERINGDB_PASSWORD,
                requireProperty("PAWVEILARBREGISTRERING_PASSWORD")
            )
            val dbHost = requireProperty("PAWVEILARBREGISTRERING_HOST")
            val dbPort = requireProperty("PAWVEILARBREGISTRERING_PORT")
            val dbName = requireProperty("PAWVEILARBREGISTRERING_DATABASE")
            System.setProperty(
                DatabaseConfig.VEILARBREGISTRERINGDB_URL,
                "jdbc:postgresql://${dbHost}:${dbPort}/${dbName}"
            )
        }
    }
}

