package no.nav.fo.veilarbregistrering.oppgave.adapter

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.log.loggerFor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.http.HttpStatus
import java.io.IOException
import java.util.concurrent.TimeUnit

class OppgaveRestClient(
    private val baseUrl: String,
    private val systemUserTokenProvider: SystemUserTokenProvider,
    private val tokenProvider: () -> String
) : HealthCheck {
    internal fun opprettOppgave(oppgaveDto: OppgaveDto): OppgaveResponseDto {
        val aadToken = tokenProvider()

        val request = Request.Builder()
            .url("$baseUrl/api/v1/oppgaver")
            .header("Authorization", "Bearer " + systemUserTokenProvider.systemUserToken)
            .method("POST", RestUtils.toJsonRequestBody(oppgaveDto))
            .build()

        val aadrequest = Request.Builder()
            .url("$baseUrl/api/v1/oppgaver")
            .header("Authorization", "Bearer $aadToken")
            .method("POST", RestUtils.toJsonRequestBody(oppgaveDto))
            .build()
        try {
            client.newCall(aadrequest).execute().use {
                if (it.code() != HttpStatus.CREATED.value()) {
                    log.warn("Opprett oppgave (AAD-token) feilet med statuskode: ${it.code()} - $it")
                } else {
                    return RestUtils.parseJsonResponseOrThrow(it, OppgaveResponseDto::class.java)
                }
            }
        }
        catch (e: IOException) {
            log.warn("Opprett oppgave (AAD-token) feilet med exception: ", e)
        }
        try {
            client.newCall(request).execute().use { response ->
                if (response.code() != HttpStatus.CREATED.value()) {
                    throw RuntimeException("Opprett oppgave feilet med statuskode: " + response.code() + " - " + response)
                }
                return RestUtils.parseJsonResponseOrThrow(response, OppgaveResponseDto::class.java)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(baseUrl, client)
    }

    companion object {
        private const val HTTP_READ_TIMEOUT = 120000

        private val client: OkHttpClient = RestClient.baseClientBuilder()
            .readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS).build()

        private val log = loggerFor<OppgaveRestClient>()
    }
}