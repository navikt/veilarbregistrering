package no.nav.fo.veilarbregistrering.orgenhet.adapter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.enhet.Kommune
import no.nav.fo.veilarbregistrering.orgenhet.HentAlleEnheterException
import okhttp3.HttpUrl
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.io.IOException

class Norg2RestClient(private val baseUrl: String, private val objectMapper: ObjectMapper) : HealthCheck {

    fun hentEnhetFor(kommune: Kommune): List<RsNavKontorDto> {
        val rsArbeidsfordelingCriteriaDto = RsArbeidsfordelingCriteriaDto(
            kommune.kommunenummer,
            RsArbeidsfordelingCriteriaDto.KONTAKT_BRUKER,
            RsArbeidsfordelingCriteriaDto.OPPFOLGING
        )
        val request = Request.Builder()
            .url("$baseUrl/api/v1/arbeidsfordeling/enheter/bestmatch")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .method("POST", RestUtils.toJsonRequestBody(rsArbeidsfordelingCriteriaDto))
            .build()

        try {
            RestClient.baseClient().newCall(request).execute().use { response ->
                if (response.code() == 404) {
                    LOG.warn("Fant ikke NavKontor for kommunenummer")
                    return emptyList()
                }
                if (!response.isSuccessful) {
                    throw RuntimeException("HentEnhetFor kommunenummer feilet med statuskode: " + response.code() + " - " + response)
                }
                val rsNavKontorDtos = RestUtils.parseJsonResponseArrayOrThrow(response, RsNavKontorDto::class.java)
                return ArrayList(rsNavKontorDtos)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun hentAlleEnheter(): List<RsEnhet> {
        val request = Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments("api/v1/enhet")
                    .addQueryParameter("oppgavebehandlerFilter", "UFILTRERT").build()
            )
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
        try {
            RestClient.baseClient().newCall(request).execute()
                .use { response -> return objectMapper.readValue(response.body()!!.byteStream(), object : TypeReference<List<RsEnhet>>() {})
                }
        } catch (e: IOException) {
            throw HentAlleEnheterException("Henting av alle enheter fra NORG2 feilet.", e)
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/internal/isAlive"), RestClient.baseClient())
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Norg2RestClient::class.java)
    }
}