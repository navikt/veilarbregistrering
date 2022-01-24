package no.nav.fo.veilarbregistrering.bruker.krr

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.http.defaultHttpClient
import no.nav.fo.veilarbregistrering.log.MDCConstants.MDC_CALL_ID
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import java.io.IOException
import javax.ws.rs.core.HttpHeaders

open class DigDirKrrProxyClient internal constructor(
    private val baseUrl: String,
    private val tokenProvider: () -> String
) : HealthCheck {
    internal open fun hentKontaktinfo(foedselsnummer: Foedselsnummer): DigDirKrrProxyResponse? {
        val request = buildRequest(foedselsnummer)
        val response = utfoer(request)
        return parse(response)
    }

    private fun buildRequest(foedselsnummer: Foedselsnummer): Request {
        return Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments("v1/person")
                    .addQueryParameter("inkluderSikkerDigitalPost", "false")
                    .build()
            )
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider())
            .header("Nav-Personident", foedselsnummer.stringValue())
            .header("Nav-Call-Id", MDC.get(MDC_CALL_ID))
            .build()
    }

    private fun utfoer(request: Request) : String {
        try {
            defaultHttpClient().newCall(request).execute().use { response -> return behandle(response) }
        } catch (e: IOException) {
            throw RuntimeException("Noe gikk galt mot DigDir-Krr-Proxy", e)
        }
    }

    @Throws(IOException::class)
    private fun behandle(response: Response): String {
        if (!response.isSuccessful) {
            val feilmelding = mapOf(
                HttpStatus.FORBIDDEN to "Ingen tilgang til forespurt ressurs",
                HttpStatus.NOT_FOUND to "Søk på kontaktinfo gav ingen treff"
            ).getOrDefault(HttpStatus.resolve(response.code()), "Noe gikk galt mot DigDir-Krr-Proxy")
            throw RuntimeException(feilmelding)
        }
        return RestUtils.getBodyStr(response).orElseThrow { RuntimeException() }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/ping"), RestClient.baseClient())
    }

    companion object {
        private val GSON = GsonBuilder().create()

        internal fun parse(jsonResponse: String): DigDirKrrProxyResponse {
            return GSON.fromJson(jsonResponse, object : TypeToken<DigDirKrrProxyResponse>() {}.type)
        }
    }
}