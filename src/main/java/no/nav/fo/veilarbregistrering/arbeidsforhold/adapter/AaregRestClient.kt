package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.io.IOException

internal open class AaregRestClient(private val baseUrl: String, private val systemUserTokenProvider: SystemUserTokenProvider) {
    /**
     * "Finn arbeidsforhold (detaljer) per arbeidstaker"
     */
    fun finnArbeidsforhold(fnr: Foedselsnummer): List<ArbeidsforholdDto> {
        val response = utforRequest(fnr)
        return parse(response)
    }

    protected open fun utforRequest(fnr: Foedselsnummer): String {
        val request = Request.Builder()
                .url(HttpUrl.parse(baseUrl)!!.newBuilder()
                        .addPathSegments("v1/arbeidstaker/arbeidsforhold")
                        .addQueryParameter("regelverk", "A_ORDNINGEN")
                        .build())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthContextHolder.requireIdTokenString())
                .header(NAV_CONSUMER_TOKEN, "Bearer " + systemUserTokenProvider.systemUserToken)
                .header(NAV_PERSONIDENT, fnr.stringValue())
                .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                .build()
        try {
            RestClient.baseClient().newCall(request).execute().use { response -> return behandleResponse(response) }
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
}