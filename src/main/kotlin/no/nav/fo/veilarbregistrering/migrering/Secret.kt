package no.nav.fo.veilarbregistrering.config

import no.nav.fo.veilarbregistrering.Application
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

object Secrets {
    operator fun get(key: String) = getCachedSecret(key)
}

private fun getCachedSecret(secretName: String): String {
    return SecretCache.getSecret(secretName)
}

private object SecretCache {
    private val existingSecrets = mutableMapOf<String, String>()

    fun getSecret(secretName: String): String {
        return existingSecrets[secretName]
            ?: run {
                val secretContent: String = if (isOnPrem()) {
                    try {
                        String(Files.readAllBytes(Paths.get(Application.SECRETS_PATH, secretName)), StandardCharsets.UTF_8)
                    } catch (e: Exception) {
                        throw IllegalStateException("Klarte ikke laste property fra vault for path: $secretName" ,e)
                    }
                } else throw IllegalStateException("Uthenting av secrets ikke implementert for clusteret ${requireClusterName()}")

                existingSecrets[secretName] = secretContent
                secretContent
            }
    }
}