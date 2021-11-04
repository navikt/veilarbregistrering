package no.nav.fo.veilarbregistrering.bruker.krr

import com.google.gson.GsonBuilder
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import okhttp3.HttpUrl
import okhttp3.Request
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import java.io.IOException
import javax.ws.rs.core.HttpHeaders

class KrrClient internal constructor(
    private val baseUrl: String,
    private val systemUserTokenProvider: SystemUserTokenProvider,
    private val tokenProvider: () -> String
) : HealthCheck {
    internal fun hentKontaktinfo(foedselsnummer: Foedselsnummer): KrrKontaktinfoDto? {
        val kontaktinfoPath = "v1/personer/kontaktinformasjon"
        val request = buildRequest(kontaktinfoPath, foedselsnummer)
        val aadRequest = buildAADRequest(kontaktinfoPath, foedselsnummer)

        try {
            RestClient.baseClient().newCall(aadRequest).execute().use { response ->
                if (!response.isSuccessful || response.code() == HttpStatus.NOT_FOUND.value()) {
                    LOG.warn("Fant ikke kontaktinfo på person i kontakt og reservasjonsregisteret med bruk av AADtoken")
                }
                val kontaktinfoDto = parse(RestUtils.getBodyStr(response).orElseThrow { RuntimeException() }, foedselsnummer)
                LOG.info("Fant kontaktinfo i krr ved kall med AADtoken: {}", kontaktinfoDto)
            }
        } catch (e: Exception) {
            LOG.warn("Feil oppsto ved kall mot krr ved bruk av AADtoken", e)
        }

        try {
            RestClient.baseClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful || response.code() == HttpStatus.NOT_FOUND.value()) {
                    LOG.warn("Fant ikke kontaktinfo på person i kontakt og reservasjonsregisteret")
                    return null
                }
                return parse(RestUtils.getBodyStr(response).orElseThrow { RuntimeException() }, foedselsnummer)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun buildRequest(path: String, foedselsnummer: Foedselsnummer): Request {
        return Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments(path)
                    .addQueryParameter("inkluderSikkerDigitalPost", "false")
                    .build()
            )
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + systemUserTokenProvider.systemUserToken)
            .header("Nav-Consumer-Id", "srvveilarbregistrering")
            .header("Nav-Personidenter", foedselsnummer.stringValue())
            .build()
    }

    private fun buildAADRequest(path: String, foedselsnummer: Foedselsnummer): Request {
        return Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments(path)
                    .addQueryParameter("inkluderSikkerDigitalPost", "false")
                    .build()
            )
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider())
            .header("Nav-Personidenter", foedselsnummer.stringValue())
            .build()
    }


    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/ping"), RestClient.baseClient())
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(KrrClient::class.java)
        private val gson = GsonBuilder().create()

        /**
         * Benytter JSONObject til parsing i parallell med GSON pga. dynamisk json.
         * @return
         */
        internal fun parse(jsonResponse: String?, foedselsnummer: Foedselsnummer): KrrKontaktinfoDto? {
            if (JSONObject(jsonResponse).has("kontaktinfo")) {
                val kontaktinfo = JSONObject(jsonResponse)
                    .getJSONObject("kontaktinfo")
                    .getJSONObject(foedselsnummer.stringValue())
                return gson.fromJson(kontaktinfo.toString(), KrrKontaktinfoDto::class.java)
            }
            if (JSONObject(jsonResponse).has("feil")) {
                val response = JSONObject(jsonResponse)
                    .getJSONObject("feil")
                    .getJSONObject(foedselsnummer.stringValue())
                val feil = gson.fromJson(response.toString(), KrrFeilDto::class.java)
                if ("Ingen kontaktinformasjon er registrert på personen" == feil.melding) {
                    return null
                }
                throw RuntimeException(String.format("Henting av kontaktinfo fra KRR feilet: %s", feil.melding))
            }
            throw RuntimeException("Ukjent feil")
        }
    }
}