package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import no.nav.common.auth.Constants
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.utils.IdentUtils
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestUtils
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.http.defaultHttpClient
import no.nav.fo.veilarbregistrering.log.MDCConstants
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.metrics.TimedMetric
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.io.IOException

open class AaregRestClient(
    private val unleashClient: UnleashClient,
    metricsService: PrometheusMetricsService,
    private val baseUrl: String,
    private val baseUrlOld: String,
    private val systemUserTokenProvider: SystemUserTokenProvider,
    private val authContextHolder: AuthContextHolder,
    private val aadTokenProvider: () -> String
) : HealthCheck, TimedMetric(metricsService) {
    /**
     * "Finn arbeidsforhold (detaljer) per arbeidstaker"
     */
    fun finnArbeidsforhold(fnr: Foedselsnummer): List<ArbeidsforholdDto> {
        logger.info("Henter arbeidsforhold...")
        if (isDevelopment()) logger.info("Issuer i token: ${authContextHolder.hentIssuer()}")
        return if (unleashClient.isEnabled("veilarbregistrering.aareg.aad") && authContextHolder.erAADToken()) {
            parse(utfoerRequestAad(fnr))
        } else {
            parse(utforRequest(fnr))
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
        val url = if (isDevelopment()) baseUrl else baseUrlOld
        val request = Request.Builder()
            .url(
                HttpUrl.parse(url)!!.newBuilder()
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

    @Throws(IOException::class)
    private fun behandleResponse(response: Response): String {
        if (!response.isSuccessful) {
            logger.info("Feilmelding fra Aareg: Message: ${response.message()} Body: ${response.body()}")
            val feilmelding = mapOf(
                HttpStatus.BAD_REQUEST to "Aareg: Ugyldig input",
                HttpStatus.UNAUTHORIZED to "Aareg: Token mangler eller er ugyldig",
                HttpStatus.FORBIDDEN to "Aareg: Ingen tilgang til forespurt ressurs",
                HttpStatus.NOT_FOUND to "Søk på arbeidforhold gav ingen treff"
            ).getOrDefault(HttpStatus.resolve(response.code()), "Noe gikk galt mot Aareg")
            throw HentArbeidsforholdException(feilmelding)
        }
        return RestUtils.getBodyStr(response).orElseThrow { HentArbeidsforholdException("Klarte ikke lese respons fra Aareg") }
    }



    companion object {
        private val GSON = GsonBuilder().create()

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
            return GSON.fromJson(json, object : TypeToken<List<ArbeidsforholdDto?>?>() {}.type)
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/ping"), defaultHttpClient())
    }

    override fun value() = "aareg"
}

private fun AuthContextHolder.erAADToken(): Boolean = hentIssuer().contains("login.microsoftonline.com")

fun AuthContextHolder.hentIssuer(): String =
    this.requireIdTokenClaims().issuer