package no.nav.fo.veilarbregistrering;

import no.nav.common.utils.SslUtils;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.runApplication
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

fun main(vararg args: String) {
    Application.readVaultSecrets()
    SslUtils.setupTruststore()
    runApplication<Application>(*args)
}

@SpringBootApplication
open class Application {

    companion object {
        private const val SECRETS_PATH = "/var/run/secrets/nais.io/"

        internal fun readVaultSecrets() {
            System.setProperty("SRVVEILARBREGISTRERING_USERNAME", getVaultSecret("serviceuser_creds/username"))
            System.setProperty("SRVVEILARBREGISTRERING_PASSWORD", getVaultSecret("serviceuser_creds/password"))

            System.setProperty(DatabaseConfig.VEILARBREGISTRERINGDB_USERNAME, getVaultSecret("oracle_creds/username"))
            System.setProperty(DatabaseConfig.VEILARBREGISTRERINGDB_PASSWORD, getVaultSecret("oracle_creds/password"))
            System.setProperty(DatabaseConfig.VEILARBREGISTRERINGDB_URL, getVaultSecret("oracle_config/jdbc_url"))
        }

        private fun getVaultSecret(path: String): String? {
            return try {
                String(Files.readAllBytes(Paths.get(SECRETS_PATH, path)), StandardCharsets.UTF_8)
            } catch (e: Exception) {
                throw IllegalStateException(String.format("Klarte ikke laste property fra vault for path: %s", path), e)
            }
        }
    }
}

