package no.nav.fo.veilarbregistrering.oppgave.adapter

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.Metric
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.http.HttpStatus
import java.io.IOException
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

class OppgaveRestClient(
    private val baseUrl: String,
    private val metricsService: PrometheusMetricsService,
    private val tokenProvider: () -> String
) : HealthCheck, Metric {
    internal fun opprettOppgave(oppgaveDto: OppgaveDto): OppgaveResponseDto {
        val aadToken = tokenProvider()

        val request = Request.Builder()
            .url("$baseUrl/api/v1/oppgaver")
            .header("Authorization", "Bearer $aadToken")
            .method("POST", RestUtils.toJsonRequestBody(oppgaveDto))
            .build()

        return doTimedCall {
            try {
                client.newCall(request).execute().use { response ->
                    if (response.code() != HttpStatus.CREATED.value()) {
                        throw RuntimeException("Opprett oppgave feilet med statuskode: " + response.code() + " - " + response)
                    } else {
                        RestUtils.parseJsonResponseOrThrow(response, OppgaveResponseDto::class.java)
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    private fun <T> doTimedCall(httpCall: () -> T): T {
        val start = Instant.now(Clock.systemDefaultZone())
        val result = httpCall()
        val end = Instant.now(Clock.systemDefaultZone())
        metricsService.registrerTimer(Events.KALL_TREDJEPART, Duration.between(start, end), this)
        return result
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(baseUrl, client)
    }

    override fun fieldName() = "tjeneste"
    override fun value() = "oppgave"

    companion object {
        private const val HTTP_READ_TIMEOUT = 120000

        private val client: OkHttpClient = RestClient.baseClientBuilder()
            .readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS).build()
    }
}