package no.nav.fo.veilarbregistrering

import no.nav.common.utils.SslUtils
import no.nav.fo.veilarbregistrering.config.ApplicationConfig
import no.nav.fo.veilarbregistrering.config.requireClusterName
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

fun main(vararg args: String) {
    Application.readVaultSecrets()
    SslUtils.setupTruststore()
    runApplication<Application>(*args)
}

@EnableAutoConfiguration
@Import(ApplicationConfig::class)
class Application {

    companion object {
        internal const val SECRETS_PATH = "/var/run/secrets/nais.io/"

        internal fun readVaultSecrets() {
            if (requireClusterName().endsWith("gcp")) {
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

            } else {
                System.setProperty("SERVICEUSER_USERNAME", getVaultSecret("serviceuser_creds/username"))
                System.setProperty("SERVICEUSER_PASSWORD", getVaultSecret("serviceuser_creds/password"))

                System.setProperty(
                    DatabaseConfig.VEILARBREGISTRERINGDB_USERNAME,
                    getVaultSecret("oracle_creds/username")
                )
                System.setProperty(
                    DatabaseConfig.VEILARBREGISTRERINGDB_PASSWORD,
                    getVaultSecret("oracle_creds/password")
                )
                System.setProperty(DatabaseConfig.VEILARBREGISTRERINGDB_URL, getVaultSecret("oracle_config/jdbc_url"))
            }
        }

        private fun getVaultSecret(path: String): String {
            return try {
                String(Files.readAllBytes(Paths.get(SECRETS_PATH, path)), StandardCharsets.UTF_8)
            } catch (e: Exception) {
                throw IllegalStateException(String.format("Klarte ikke laste property fra vault for path: %s", path), e)
            }
        }
    }
}

