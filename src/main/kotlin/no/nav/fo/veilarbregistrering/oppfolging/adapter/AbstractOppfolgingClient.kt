package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.fo.veilarbregistrering.feil.ForbiddenException
import no.nav.fo.veilarbregistrering.feil.RestException
import no.nav.fo.veilarbregistrering.http.Headers.buildHeaders
import no.nav.fo.veilarbregistrering.http.buildHttpClient
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.metrics.TimedMetric
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

abstract class AbstractOppfolgingClient(
    private val objectMapper: ObjectMapper,
    metricsService: MetricsService
): TimedMetric(metricsService) {

    fun <T, R : RuntimeException> executeRequest(
        request: Request,
        responseClass: Class<T>,
        expectedErrorsHandler: (Exception) -> R? = emptyHandler
    ): T {
        try {
            client.newCall(request).execute().use { response ->
                when (response.code()) {
                    in 200..299 -> {
                        response.body()?.string()?.let { bodyString ->
                            return objectMapper.readValue(bodyString, responseClass)
                        } ?: throw RuntimeException("Unexpected empty body")
                    }
                    403 -> throw ForbiddenException(response.body()?.string())
                    else -> throw RestException(response.code())
                }
            }
        } catch (e: Exception) {
            runExceptionmapperAndThrow(expectedErrorsHandler, e, request.method(), request.url().toString())
        }
    }

    fun buildRequest(url: String, headers: List<Pair<String, String>>): Request.Builder =
        Request.Builder().url(url).also { r ->
            r.headers(buildHeaders(headers))
        }

    fun <R : RuntimeException> runExceptionmapperAndThrow(
        expectedErrorsHandler: (Exception) -> R?,
        e: Exception,
        method: String,
        url: String
    ): Nothing {
        when (val mappedException = expectedErrorsHandler.invoke(e)) {
            is R -> {
                throw mappedException
            }
            else -> {
                throw RuntimeException("Feil ved [$method] mot [$url]", e)
            }
        }
    }

    override fun value() = "veilarboppfolging"

    companion object {
        val client: OkHttpClient = buildHttpClient {
            readTimeout(120L, TimeUnit.SECONDS)
        }

        val emptyHandler: (Exception) -> Nothing? = { null }
    }

}
