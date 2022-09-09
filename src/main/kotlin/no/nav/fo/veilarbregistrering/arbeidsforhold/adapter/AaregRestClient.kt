package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import com.fasterxml.jackson.core.type.TypeReference
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestUtils
import no.nav.common.sts.SystemUserTokenProvider
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
import no.nav.fo.veilarbregistrering.tokenveksling.erAADToken
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
    private val systemUserTokenProvider: SystemUserTokenProvider,
    private val authContextHolder: AuthContextHolder,
    private val tokenExchangeService: TokenExchangeService,
    private val aadTokenProvider: () -> String
) : HealthCheck, TimedMetric(metricsService) {
    /**
     * "Finn arbeidsforhold (detaljer) per arbeidstaker"
     */
    fun finnArbeidsforhold(fnr: Foedselsnummer): List<ArbeidsforholdDto> {
        return if (authContextHolder.erAADToken()) {
            parse(utfoerRequestAad(fnr))
        } else {
            var nyAaregRespons: List<ArbeidsforholdDto>? = null
            try {
                nyAaregRespons = parse(utforRequestTokenX(fnr))
                logger.info("Kall til Aareg med TokenX-veksling er OK")
            } catch (e: Exception) {
                logger.warn("Feil i request mot Aareg med TokenX-veksling: ${e.message}", e)
            }

            val opprinneligRespons = parse(utforRequest(fnr))
            if (nyAaregRespons != opprinneligRespons) {
                logger.warn("Avvik i respons fra Aareg med og uten tokenX-veksling. Med tokenX: $nyAaregRespons, opprinnelig kall: $opprinneligRespons")
            }
            opprinneligRespons
        }
    }

    protected open fun utfoerRequestAad(fnr: Foedselsnummer): String {
        val request = Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments("v1/arbeidstaker/arbeidsforhold")
                    .addQueryParameter("regelverk", "A_ORDNINGEN")
                    .build()
            )
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${aadTokenProvider()}")
            .header(NAV_PERSONIDENT, fnr.stringValue())
            .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
            .build()

        return doTimedCall {
            try {
                defaultHttpClient().newCall(request).execute()
                    .use { response -> behandleResponse(response) }
            } catch (e: IOException) {
                throw HentArbeidsforholdException("Noe gikk galt mot Aareg", e)
            }
        }
    }

    protected open fun utforRequest(fnr: Foedselsnummer): String {
        val request = Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments("v1/arbeidstaker/arbeidsforhold")
                    .addQueryParameter("regelverk", "A_ORDNINGEN")
                    .build()
            )
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${authContextHolder.requireIdTokenString()}")
            .header(NAV_CONSUMER_TOKEN, "Bearer ${systemUserTokenProvider.systemUserToken}")
            .header(NAV_PERSONIDENT, fnr.stringValue())
            .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
            .build()

        return doTimedCall {
            try {
                defaultHttpClient().newCall(request).execute().use { response -> behandleResponse(response) }
            } catch (e: IOException) {
                throw HentArbeidsforholdException("Noe gikk galt mot Aareg", e)
            }
        }
    }

    protected open fun utforRequestTokenX(fnr: Foedselsnummer): String {
        val request = Request.Builder()
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

        return doTimedCall {
            try {
                defaultHttpClient().newCall(request).execute().use { response -> behandleResponse(response) }
            } catch (e: IOException) {
                throw HentArbeidsforholdException("Noe gikk galt mot Aareg", e)
            }
        }
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
         * Token for konsument fra Nav Security Token Service (STS) - brukes til autentisering
         * og autorisasjon for tjenestekall - angis med ‘Bearer + mellomrom + token’
         */
        private const val NAV_CONSUMER_TOKEN = "Nav-Consumer-Token"

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
