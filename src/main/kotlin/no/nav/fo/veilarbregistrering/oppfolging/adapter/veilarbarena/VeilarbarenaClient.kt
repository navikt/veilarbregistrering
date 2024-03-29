package no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.http.RetryInterceptor
import no.nav.fo.veilarbregistrering.http.buildHttpClient
import no.nav.fo.veilarbregistrering.http.defaultHttpClient
import no.nav.fo.veilarbregistrering.log.secureLogger
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.metrics.TimedMetric
import no.nav.fo.veilarbregistrering.oppfolging.SammensattOppfolgingStatusException
import okhttp3.Request
import org.springframework.http.HttpStatus
import java.io.IOException

class VeilarbarenaClient(
    private val baseUrl: String,
    metricsService: MetricsService,
    private val veilarbarenaTokenProvider: () -> String
) : HealthCheck, TimedMetric(metricsService) {

    internal fun arenaStatus(fnr: Foedselsnummer): ArenaStatusDto? {
        val veilarbarenaToken = veilarbarenaTokenProvider()

        if (isDevelopment()) {
            secureLogger.info("veilarbarenaToken: $veilarbarenaToken")
        }

        val request = Request.Builder()
            .url("$baseUrl/arena/status?fnr=${fnr.stringValue()}")
            .header("Authorization", "Bearer $veilarbarenaToken")
            .build()

        val httpClient = buildHttpClient { addInterceptor(RetryInterceptor()) }

        return doTimedCall {
            try {
                httpClient.newCall(request).execute().use { response ->
                    when (response.code()) {
                        404 -> null
                        in 300..599 -> throw SammensattOppfolgingStatusException("Henting av arena status for bruker feilet: ${response.code()} - $response")
                        else -> response.body()?.string()?.let { objectMapper.readValue(it) }
                            ?: throw SammensattOppfolgingStatusException("Henting av arenastatus returnerte tom body")
                    }
                }
            } catch (e: IOException) {
                throw SammensattOppfolgingStatusException("Uventet feil mot veilarbarena: ${e.message}", e)
            }
        }
    }

    internal fun kanReaktiveres(fnr: Foedselsnummer): KanReaktiveresDto {
        val veilarbarenaToken = veilarbarenaTokenProvider()

        val request = Request.Builder()
            .url("$baseUrl/arena/kan-enkelt-reaktiveres?fnr=${fnr.stringValue()}")
            .header("Authorization", "Bearer $veilarbarenaToken")
            .build()

        val httpClient = buildHttpClient { addInterceptor(RetryInterceptor()) }

        return doTimedCall {
            try {
                    httpClient.newCall(request).execute().use { response ->
                    if (response.code() != HttpStatus.OK.value()) {
                        throw SammensattOppfolgingStatusException("Henting av arena status for bruker feilet: " + response.code() + " - " + response)
                    } else {
                        response.body()?.string()?.let { objectMapper.readValue(it) }
                            ?: throw SammensattOppfolgingStatusException("Henting av arenastatus returnerte tom body")
                    }
                }
            } catch (e: IOException) {
                throw SammensattOppfolgingStatusException("Uventet feil mot veilarbarena: ${e.message}", e)
            }
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(baseUrl, defaultHttpClient())
    }

    override fun value() = "veilarbarena"
}

data class ArenaStatusDto(val formidlingsgruppe: String, val kvalifiseringsgruppe: String, val rettighetsgruppe: String)
data class KanReaktiveresDto(val kanEnkeltReaktiveres: Boolean?)
