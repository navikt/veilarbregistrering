package no.nav.fo.veilarbregistrering.oppfolging.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.metrics.MetricsClient
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.fo.veilarbregistrering.feil.ForbiddenException
import no.nav.fo.veilarbregistrering.feil.RestException
import no.nav.fo.veilarbregistrering.metrics.Event
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsFilter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

abstract class AbstractOppfolgingClient(private val objectMapper: ObjectMapper, private val metricsClient: MetricsClient) {

    fun <R : RuntimeException> post(
            url: String,
            requestEntity: Any,
            headers: List<Pair<String, String>> = emptyList(),
            event: Event,
            expectedErrorsHandler: (Exception) -> R?
    ) {
        val request: Request.Builder = buildRequest(url, headers)
        request.method(
                "POST",
                requestEntity.let { RequestBody.create(RestUtils.MEDIA_TYPE_JSON, objectMapper.writeValueAsString(it)) }
        )

        try {
            clientWithMetricsFilter(metricsClient, event).newCall(request.build()).execute().use { response ->
                when (val code = response.code()) {
                    204 -> return@use
                    403 -> throw ForbiddenException(response.body()?.string())
                    else -> throw RestException(code)
                }
            }
        } catch (e: Exception) {
            runExceptionmapperAndThrow(expectedErrorsHandler, e, "POST", url)
        }
    }

    fun <T, R : RuntimeException> get(
        url: String,
        headers: List<Pair<String, String>> = emptyList(),
        responseClass: Class<T>,
        event: Event,
        expectedErrorsHandler: (Exception) -> R?
    ): T {
        return executeRequest(buildRequest(url, headers).build(), responseClass, event, expectedErrorsHandler)
    }

    private fun <T, R : RuntimeException> executeRequest(
        request: Request,
        responseClass: Class<T>,
        event: Event,
        expectedErrorsHandler: (Exception) -> R? = emptyHandler
    ): T {
        try {
            clientWithMetricsFilter(metricsClient, event).newCall(request).execute().use { response ->
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

    private fun buildRequest(url: String, headers: List<Pair<String, String>>): Request.Builder =
            Request.Builder().url(url).also { r ->
                headers.forEach { (k, v) -> r.header(k, v) }
            }

    private fun <R : RuntimeException> runExceptionmapperAndThrow(
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

    companion object {
        val baseClientBuilder: OkHttpClient.Builder = RestClient.baseClientBuilder()
            .readTimeout(120L, TimeUnit.SECONDS)


        fun clientWithMetricsFilter(metricsClient: MetricsClient, event: Event) = baseClientBuilder
            .addInterceptor(InfluxMetricsFilter(metricsClient, event))
            .build()

        val emptyHandler: (Exception) -> Nothing? = { null }
    }

}
