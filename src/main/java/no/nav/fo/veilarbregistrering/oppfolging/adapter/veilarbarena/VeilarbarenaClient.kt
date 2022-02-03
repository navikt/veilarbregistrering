package no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.http.defaultHttpClient
import okhttp3.Request
import org.springframework.http.HttpStatus
import java.io.IOException

class VeilarbarenaClient(
    private val baseUrl: String,
    private val tokenProvider: () -> String
) : HealthCheck {
    internal fun arenaStatus(fnr: Foedselsnummer): ArenaStatusDto {
        val aadToken = tokenProvider()

        val request = Request.Builder()
            .url("$baseUrl/api/arena/status?fnr=${fnr.stringValue()}")
            .header("Authorization", "Bearer $aadToken")
            .build()

        return try {
            defaultHttpClient().newCall(request).execute().use { response ->
                if (response.code() != HttpStatus.OK.value()) {
                    throw SammensattOppfolgingStatusException("Henting av arena status for bruker feilet: " + response.code() + " - " + response)
                } else {
                    response.body()?.string()?.let { objectMapper.readValue(it) }
                        ?: throw SammensattOppfolgingStatusException("Henting av arenastatus returnerte tom body")
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    internal fun kanReaktiveres(fnr: Foedselsnummer): KanReaktiveresDto {
        val aadToken = tokenProvider()

        val request = Request.Builder()
            .url("$baseUrl/api/arena/kan-enkelt-reaktiveres?fnr=${fnr.stringValue()}")
            .header("Authorization", "Bearer $aadToken")
            .build()

        return try {
            defaultHttpClient().newCall(request).execute().use { response ->
                if (response.code() != HttpStatus.OK.value()) {
                    throw SammensattOppfolgingStatusException("Henting av arena status for bruker feilet: " + response.code() + " - " + response)
                } else {
                    response.body()?.string()?.let { objectMapper.readValue(it) }
                        ?: throw SammensattOppfolgingStatusException("Henting av arenastatus returnerte tom body")
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(baseUrl, defaultHttpClient())
    }
}

class ArenaStatusDto(val formidlingsgruppe: String, val kvalifiseringsgruppe: String, val rettighetsgruppe: String)
class KanReaktiveresDto(val kanEnkeltReaktiveres: Boolean)
