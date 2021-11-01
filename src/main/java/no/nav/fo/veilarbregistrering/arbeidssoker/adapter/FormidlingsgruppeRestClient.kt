package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import com.google.gson.*
import no.nav.common.health.HealthCheck
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingsgruppeResponseDto
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingsgruppeRestClient
import java.lang.RuntimeException
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import java.io.IOException
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.HealthCheckUtils
import no.nav.common.utils.UrlUtils
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import kotlin.Throws
import org.springframework.lang.Nullable
import java.lang.reflect.Type
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class FormidlingsgruppeRestClient internal constructor(
    private val baseUrl: String,
    private val arenaOrdsTokenProvider: Supplier<String>
) : HealthCheck {
    fun hentFormidlingshistorikk(
        foedselsnummer: Foedselsnummer,
        periode: Periode
    ): FormidlingsgruppeResponseDto? {
        return try {
            val response = utfoerRequest(foedselsnummer, periode)

            response?.let(::parse) ?: run {
                LOG.warn("Søk på fødselsnummer gav ingen treff i Arena")
                null
            }
        } catch (e: RuntimeException) {
            throw RuntimeException("Hent formidlingshistorikk feilet", e)
        }
    }

    private fun utfoerRequest(foedselsnummer: Foedselsnummer, periode: Periode): String? {
        val request = Request.Builder()
            .url(
                HttpUrl.parse(baseUrl)!!.newBuilder()
                    .addPathSegments("/api/v1/person/arbeidssoeker/formidlingshistorikk")
                    .addQueryParameter("fnr", foedselsnummer.stringValue())
                    .addQueryParameter(
                        "fraDato",
                        periode.fraDatoAs_yyyyMMdd()
                    ) //TODO: null-sjekk på tilDato - skal ikke alltid med
                    .addQueryParameter("tilDato", periode.tilDatoAs_yyyyMMdd())
                    .build()
            )
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + arenaOrdsTokenProvider.get())
            .build()
        val httpClient =
            RestClient.baseClient().newBuilder().readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS).build()
        try {
            httpClient.newCall(request).execute().use { response ->
                return when {
                    response.code() == HttpStatus.NOT_FOUND.value() -> null
                    !response.isSuccessful -> throw RuntimeException("Feilkode: " + response.code())
                    else -> RestUtils.getBodyStr(response)
                        .orElseThrow { RuntimeException("Feil ved uthenting av response body") }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun checkHealth(): HealthCheckResult {
        return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(baseUrl, "/api/v1/test/ping"), RestClient.baseClient())
    }

    private class LocalDateDeserializer : JsonDeserializer<LocalDate?> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate? {
            return Optional.ofNullable(json.asJsonPrimitive.asString)
                .map { text: String? -> LocalDate.parse(text) }
                .orElse(null)
        }
    }

    companion object {
        private const val HTTP_READ_TIMEOUT = 120000
        private val LOG = LoggerFactory.getLogger(FormidlingsgruppeRestClient::class.java)
        private val GSON = GsonBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer()).create()
        fun parse(jsonResponse: String?): FormidlingsgruppeResponseDto {
            return GSON.fromJson(jsonResponse, FormidlingsgruppeResponseDto::class.java)
        }
    }
}