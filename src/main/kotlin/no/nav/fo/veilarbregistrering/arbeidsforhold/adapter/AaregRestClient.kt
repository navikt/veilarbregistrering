package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestUtils
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.http.defaultHttpClient
import no.nav.fo.veilarbregistrering.log.MDCConstants
import no.nav.fo.veilarbregistrering.log.logger
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
    private val baseUrl: String,
    private val baseUrlOld: String,
    private val systemUserTokenProvider: SystemUserTokenProvider,
    private val authContextHolder: AuthContextHolder,
    private val tokenProvider: () -> String
) : HealthCheck {
    /**
     * "Finn arbeidsforhold (detaljer) per arbeidstaker"
     */
    fun finnArbeidsforhold(fnr: Foedselsnummer): List<ArbeidsforholdDto> {
        val response = utforRequest(fnr)

        if (unleashClient.isEnabled("veilarbregistrering.aareg.aad")) {
            val responseAad = utfoerRequestAad(fnr)
            sammenliknResponser(responseAad, response)
        }

        return parse(response)
    }

    private fun sammenliknResponser(responseAad: String, response: String) {
        if (responseAad != response) logger.warn("Respons fra ny og gammel Aareg divergerer")
    }

    protected open fun utfoerRequestAad(fnr: Foedselsnummer) : String {
        val request = Request.Builder()
            .url(HttpUrl.parse(baseUrl)!!.newBuilder()
                .addPathSegments("v1/arbeidstaker/arbeidsforhold")
                .addQueryParameter("regelverk", "A_ORDNINGEN")
                .build())
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenProvider()}")
            .header(NAV_PERSONIDENT, fnr.stringValue())
            .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
            .build()

        return try {
            defaultHttpClient().newCall(request).execute().use { response -> behandleResponse(response) }
        } catch (e: Exception) {
            logger.warn("Nytt kall til Aareg feilet", e)
            "No response"
        }
    }

    protected open fun utforRequest(fnr: Foedselsnummer): String {
        val request = Request.Builder()
                .url(HttpUrl.parse(baseUrlOld)!!.newBuilder()
                        .addPathSegments("v1/arbeidstaker/arbeidsforhold")
                        .addQueryParameter("regelverk", "A_ORDNINGEN")
                        .build())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authContextHolder.requireIdTokenString())
                .header(NAV_CONSUMER_TOKEN, "Bearer " + systemUserTokenProvider.systemUserToken)
                .header(NAV_PERSONIDENT, fnr.stringValue())
                .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                .build()
        try {
            defaultHttpClient().newCall(request).execute().use { response -> return behandleResponse(response) }
        } catch (e: IOException) {
            throw RuntimeException("Noe gikk galt mot Aareg", e)
        }
    }

    @Throws(IOException::class)
    private fun behandleResponse(response: Response): String {
        if (!response.isSuccessful) {
            val feilmelding = mapOf(
                    HttpStatus.BAD_REQUEST to "Ugyldig input",
                    HttpStatus.UNAUTHORIZED to "Token mangler eller er ugyldig",
                    HttpStatus.FORBIDDEN to "Ingen tilgang til forespurt ressurs",
                    HttpStatus.NOT_FOUND to "Søk på arbeidforhold gav ingen treff"
            ).getOrDefault(HttpStatus.resolve(response.code()), "Noe gikk galt mot Aareg")
            throw RuntimeException(feilmelding)
        }
        return RestUtils.getBodyStr(response).orElseThrow { RuntimeException() }
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
}