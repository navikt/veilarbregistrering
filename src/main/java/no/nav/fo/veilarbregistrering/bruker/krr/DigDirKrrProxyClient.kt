package no.nav.fo.veilarbregistrering.bruker.krr

import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.AaregRestClient
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdDto
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.feil.ForbiddenException
import no.nav.fo.veilarbregistrering.log.MDCConstants.MDC_CALL_ID
import okhttp3.HttpUrl
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import java.io.IOException
import javax.ws.rs.core.HttpHeaders

class DigDirKrrProxyClient internal constructor(
    private val baseUrl: String,
    private val tokenProvider: () -> String
) : HealthCheck {
    internal fun hentKontaktinfo(foedselsnummer: Foedselsnummer): DigDirKrrProxyResponse? {
        val kontaktinfoPath = "v1/person"
        val request = buildRequest(kontaktinfoPath, foedselsnummer)

        try {
            RestClient.baseClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    when(response.code()) {
                        HttpStatus.NOT_FOUND.value() -> LOG.warn(
                            "Fant ikke kontaktinfo på person i kontakt og reservasjonsregisteret med bruk av AADtoken: {}",
                            response
                        )
                        HttpStatus.FORBIDDEN.value() -> LOG.warn(
                            "Fant ikke kontaktinfo på person i kontakt og reservasjonsregisteret med bruk av AADtoken: {}",
                            response
                        )
                    }
                    return null
                }

                val body = RestUtils.getBodyStr(response).orElseThrow(::RuntimeException)
                return parse(body)
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
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider())
            .header("Nav-Personident", foedselsnummer.stringValue())
            .header("Nav-Call-Id", MDC.get(MDC_CALL_ID))
            .build()
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/ping"), RestClient.baseClient())
    }

    companion object {
        private val GSON = GsonBuilder().create()
        private val LOG = LoggerFactory.getLogger(DigDirKrrProxyClient::class.java)

        private fun parse(json: String): DigDirKrrProxyResponse {
            return GSON.fromJson(json, object : TypeToken<DigDirKrrProxyResponse>() {}.type)
        }
    }
}