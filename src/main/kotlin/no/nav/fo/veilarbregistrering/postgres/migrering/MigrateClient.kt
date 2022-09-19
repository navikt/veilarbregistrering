package no.nav.fo.veilarbregistrering.postgres.migrering

import com.fasterxml.jackson.core.type.TypeReference
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.io.IOException
import java.lang.System.getenv
import java.util.concurrent.TimeUnit

@Component
@Profile("gcp")
class MigrateClient {

    fun hentNesteBatchFraTabell(tabell: TabellNavn, sisteIndex: Int): List<MutableMap<String, Any>> {
        val request: Request = buildRequest("$VEILARBREGISTRERING_URL/api/migrering?tabellNavn=${tabell.name}&idSisthentet=${sisteIndex}")

        try {
            restClient.newCall(request).execute().use { response ->
                if (response.code() == 404) {
                    logger.error("Fant ikke tabell")
                }
                if (!response.isSuccessful) {
                    throw RuntimeException(
                        "Henting av rader feilet med statuskode: " + response.code()
                            .toString() + " - " + response
                    )
                }

                response.body()?.let { body ->
                    val databaserader = objectMapper.readValue(body.byteStream(), object : TypeReference<List<MutableMap<String, Any>>>() {})
                    logger.info("Hentet ${databaserader.size} rader for $tabell fra index $sisteIndex.")
                    return databaserader
                } ?: throw RuntimeException("Forventet respons med body, men mottok ingenting")
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun hentSjekkerForTabell(tabell: TabellNavn): List<Map<String, Any>> {
        try {
            restClient.newCall(buildRequest("$VEILARBREGISTRERING_URL/api/migrering/sjekksum/${tabell.name}"))
                .execute().use { response ->
                response.body()?.let { body ->
                    val byteStream = body.byteStream()
                    val str = body.string()
                    logger.info("${tabell.name}.json: $str")
                    return objectMapper.readValue(byteStream, object : TypeReference<List<Map<String, Any>>>() {})
                } ?: throw RuntimeException("Forventet respons med body, men mottok ingenting")
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun hentAntallPotensieltOppdaterteTilstander(): Int =
        try {
            restClient.newCall(
                buildRequest("$VEILARBREGISTRERING_URL/api/migrering/registrering-tilstand/antall-potensielt-oppdaterte")
            )
                .execute().use { response ->
                    response.body()?.let { body ->
                        val byteStream = body.byteStream()
                        val bodyString = body.string()
                        logger.info("Antall tilstander: $bodyString")
                        objectMapper.readValue(byteStream, object : TypeReference<Map<String, Int>>() {})
                    }
                }?.get("antall") ?: throw RuntimeException("Forventet respons med body, men mottok ingenting")
        } catch (e: IOException) {
            0
        }

    fun hentOppdaterteRegistreringStatuser(trengerOppdatering: List<RegistreringTilstand>): List<Map<String, Any>> {
        val map = trengerOppdatering.associate { it.id to it.status }

        try {
            restClient.newCall(
                requestBuilder("$VEILARBREGISTRERING_URL/api/migrering/registrering-tilstand/hent-oppdaterte-statuser")
                    .post(RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(map)))
                    .build()
            ).execute().use { response ->
                response.body()?.let { body ->
                    val oppdaterteTilstander: List<Map<String, Any>> = objectMapper.readValue(body.byteStream(), object : TypeReference<List<Map<String, Any>>>() {})
                    logger.info("Hentet ${oppdaterteTilstander.size} oppdaterte tilstander")
                    return oppdaterteTilstander
                }
            } ?: throw RuntimeException("Forventet respons med body, men mottok ingenting")
        } catch (e: IOException) {
            logger.error("Error while getting updated statuses", e)
            return emptyList()
        }
    }

    companion object {
        private fun buildRequest(url: String) =
                requestBuilder(url)
                .build()

        private fun requestBuilder(url: String) =
            Request.Builder()
                .url(url)
                .header("accept", "application/json")
                .header("x_consumerId", "veilarbregistrering")
                .header("x-token", getenv("MIGRATION_TOKEN"))

        private val restClient = OkHttpClient.Builder()
            .readTimeout(240L, TimeUnit.SECONDS)
            .followRedirects(false)
            .build()

        val VEILARBREGISTRERING_URL = requireProperty("VEILARBREGISTRERING_ONPREM_URL")
    }
}