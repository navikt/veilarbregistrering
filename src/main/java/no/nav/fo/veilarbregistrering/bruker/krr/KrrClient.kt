package no.nav.fo.veilarbregistrering.bruker.krr

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.objectMapper
import okhttp3.HttpUrl
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import java.io.IOException
import javax.ws.rs.core.HttpHeaders

class KrrClient internal constructor(
    private val baseUrl: String,
    private val tokenProvider: () -> String
) : HealthCheck {
    internal fun hentKontaktinfo(foedselsnummer: Foedselsnummer): KrrKontaktInfo? {
        val kontaktinfoPath = "v1/personer/kontaktinformasjon"
        val request = buildRequest(kontaktinfoPath, foedselsnummer)

        try {
            RestClient.baseClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful || response.code() == HttpStatus.NOT_FOUND.value()) {
                    LOG.warn("Fant ikke kontaktinfo på person i kontakt og reservasjonsregisteret med bruk av AADtoken: {}", response)
                    return null
                }else {
                    val body = RestUtils.getBodyStr(response).orElseThrow(::RuntimeException)
                    return parse(body)
                }
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
            .header("Nav-Personidenter", foedselsnummer.stringValue())
            .build()
    }


    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/ping"), RestClient.baseClient())
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(KrrClient::class.java)

        internal fun parse(body: String): KrrKontaktInfo? =
            objectMapper.readValue<KrrKontaktInfoResponse>(body).let { kontaktInfo ->
                kontaktInfo.feil?.values?.any { feil ->
                    if (feil.melding == "Ingen kontaktinformasjon er registrert på personen") return@let null else
                        throw RuntimeException("Henting av kontaktinfo fra KRR feilet: ${feil.melding}")
                }
                kontaktInfo.kontaktinfo?.values?.firstOrNull()
            }

    }
}