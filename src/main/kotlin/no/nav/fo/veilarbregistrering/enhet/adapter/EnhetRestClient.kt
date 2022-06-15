package no.nav.fo.veilarbregistrering.enhet.adapter

import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.log.loggerFor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

open class EnhetRestClient(val baseUrl: String) {
    private val url: String = "$baseUrl/api/v1/organisasjon/"

    open fun hentOrganisasjon(organisasjonsnummer: Organisasjonsnummer): OrganisasjonDetaljerDto? {
        val request = Request.Builder()
                .url(url + organisasjonsnummer.asString())
                .build()

        return try {
            client.newCall(request).execute().use { response ->
                when (response.code()) {
                    in 200..399 -> {
                        val body = RestUtils.getBodyStr(response).orElseThrow { RuntimeException() }
                        parse(body).organisasjonDetaljer
                    }
                    404 -> {
                        LOG.warn("Fant ikke organisasjon for organisasjonsnummer: $organisasjonsnummer")
                        null
                    }
                    else -> throw RuntimeException("Hent organisasjon feilet med status: ${response.code()}")
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Hent organsisasjon feilet", e)
        }
    }

    companion object {
        private const val HTTP_READ_TIMEOUT: Long = 120000
        private val LOG = loggerFor<EnhetRestClient>()
        private val client: OkHttpClient =
                RestClient.baseClientBuilder().readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS).build()

        fun parse(jsonResponse: String): OrganisasjonDto {
            return objectMapper.readValue(jsonResponse, OrganisasjonDto::class.java)
        }
    }
}