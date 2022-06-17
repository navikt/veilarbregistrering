package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.config.parse
import no.nav.fo.veilarbregistrering.http.buildHttpClient
import no.nav.fo.veilarbregistrering.http.defaultHttpClient
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.metrics.TimedMetric
import okhttp3.HttpUrl
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class FormidlingsgruppeRestClient internal constructor(
    private val baseUrl: String,
    metricsService: MetricsService,
    private val arenaOrdsTokenProvider: Supplier<String>
) : HealthCheck, TimedMetric(metricsService) {

    fun hentFormidlingshistorikkVersjon2(
        foedselsnummer: Foedselsnummer,
        periode: Periode
    ): FormidlingsgruppeResponseDto? {
        val request = buildRequest(foedselsnummer, periode)
        val httpClient = buildHttpClient { readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS) }
        return doTimedCall {
            httpClient.newCall(request).execute().use {
                if (it.isSuccessful) {
                    it.body()?.string()?.let {
                        objectMapper.readValue(it, FormidlingsgruppeResponseDto::class.java)
                    } ?: throw RuntimeException("Unexpected empty body")

                } else {
                    val status = HttpStatus.valueOf(it.code())
                    when (status) {
                        HttpStatus.NOT_FOUND -> null
                        else -> throw RuntimeException("Hent formidlingshistorikk fra Arena feilet med statuskode: $status")
                    }
                }
            }
        }
    }

    fun hentFormidlingshistorikk(
        foedselsnummer: Foedselsnummer,
        periode: Periode
    ): FormidlingsgruppeResponseDto? {
        return doTimedCall {
            try {
                val response = utfoerRequest(foedselsnummer, periode)

                response?.let(::parse) ?: run {
                    LOG.warn("Søk på fødselsnummer gav ingen treff i Arena")
                    null
                }
            } catch (e: RuntimeException) {
                throw RuntimeException("Hent formidlingshistorikk feilet", e)
            }
        }
    }

    private fun utfoerRequest(foedselsnummer: Foedselsnummer, periode: Periode): String? {
        val request = buildRequest(foedselsnummer, periode)
        val httpClient = buildHttpClient { readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS) }

        try {
            httpClient.newCall(request).execute().use { response ->
                return when {
                    response.code() == HttpStatus.NOT_FOUND.value() -> null
                    !response.isSuccessful -> throw RuntimeException("Feilkode: " + response.code())
                    else -> RestUtils.getBodyStr(response)
                        .orElseThrow { RuntimeException("Feil ved uthenting av response body") }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun buildRequest(foedselsnummer: Foedselsnummer, periode: Periode): Request {
        return Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments("v1/person/arbeidssoeker/formidlingshistorikk")
                    .addQueryParameter("fnr", foedselsnummer.stringValue())
                    .addQueryParameter("fraDato", periode.fraDatoSomUtcString())
                    .addQueryParameter("tilDato", periode.tilDatoSomUtcString())
                    .build()
            )
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + arenaOrdsTokenProvider.get())
            .build()
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "v1/test/ping"), defaultHttpClient())
    }

    companion object {
        private const val HTTP_READ_TIMEOUT = 120000
        private val LOG = LoggerFactory.getLogger(FormidlingsgruppeRestClient::class.java)
    }

    override fun value() = "arenaords"
}