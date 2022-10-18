package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import com.fasterxml.jackson.core.type.TypeReference
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.arbeidsforhold.HentArbeidsforholdException
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.http.defaultHttpClient
import no.nav.fo.veilarbregistrering.log.MDCConstants
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.metrics.TimedMetric
import no.nav.fo.veilarbregistrering.tokenveksling.TokenExchangeService
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.io.IOException

open class AaregRestClient(
    metricsService: MetricsService,
    private val baseUrl: String,
    private val tokenExchangeService: TokenExchangeService
) : HealthCheck, TimedMetric(metricsService) {
    /**
     * "Finn arbeidsforhold (detaljer) per arbeidstaker"
     */
    fun finnArbeidsforhold(fnr: Foedselsnummer): List<ArbeidsforholdDto> {
        val request = buildRequest(fnr)
        return parse(utfoerRequest(request))
    }

    protected open fun utfoerRequest(request: Request): String {
        return doTimedCall {
            try {
                defaultHttpClient().newCall(request).execute()
                    .use { response -> behandleResponse(response) }
            } catch (e: IOException) {
                throw HentArbeidsforholdException("Noe gikk galt mot Aareg", e)
            }
        }
    }

    protected open fun buildRequest(fnr: Foedselsnummer): Request {
        return Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments("v1/arbeidstaker/arbeidsforhold")
                    .addQueryParameter("regelverk", "A_ORDNINGEN")
                    .build()
            )
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenExchangeService.exchangeToken(aaregApi)}")
            .header(NAV_PERSONIDENT, fnr.stringValue())
            .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
            .build()
    }

    @Throws(IOException::class)
    private fun behandleResponse(response: Response): String {
        if (!response.isSuccessful) {
            logger.info("Feilmelding fra Aareg: Message: ${response.message()} Body: ${RestUtils.getBodyStr(response)}")
            val feilmelding = mapOf(
                HttpStatus.BAD_REQUEST to "Aareg: Ugyldig input",
                HttpStatus.UNAUTHORIZED to "Aareg: Token mangler eller er ugyldig",
                HttpStatus.FORBIDDEN to "Aareg: Ingen tilgang til forespurt ressurs",
                HttpStatus.NOT_FOUND to "Søk på arbeidforhold gav ingen treff"
            ).getOrDefault(HttpStatus.resolve(response.code()), "Noe gikk galt mot Aareg")
            throw HentArbeidsforholdException(feilmelding)
        }
        return RestUtils.getBodyStr(response)
            .orElseThrow { HentArbeidsforholdException("Klarte ikke lese respons fra Aareg") }
    }


    companion object {

        /**
         * Identifikator for arbeidstaker (FNR/DNR/Aktør-id)
         */
        private const val NAV_PERSONIDENT = "Nav-Personident"
        private const val NAV_CALL_ID_HEADER = "Nav-Call-Id"
        private fun parse(json: String): List<ArbeidsforholdDto> {
            return objectMapper.readValue(json, object : TypeReference<List<ArbeidsforholdDto>>() {})
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/ping"), defaultHttpClient())
    }

    override fun value() = "aareg"
}
