package no.nav.fo.veilarbregistrering.migrering.konsument.adapter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.TabellNavn
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateClient
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class GcpMigrateClient(private val baseUrl: String) : MigrateClient {

    override fun hentNesteBatchFraTabell(tabell: TabellNavn, sisteIndex: Int): List<MutableMap<String, Any>> {
        val request: Request = buildRequest("$baseUrl/api/migrering?tabellNavn=${tabell.name}&idSisthentet=${sisteIndex}")

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
                    val databaserader = Gson().fromJson<List<MutableMap<String, Any>>>(body.string())
                    logger.info("Hentet ${databaserader.size} rader for ${tabell} fra index ${sisteIndex}.")
                    return databaserader
                } ?: throw RuntimeException("Forventet respons med body, men mottok ingenting")
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun hentSjekkerForTabell(tabell: TabellNavn): List<Map<String, Any>> {
        try {
            restClient.newCall(buildRequest("$baseUrl/api/migrering/sjekksum/${tabell.name}"))
                .execute().use { response ->
                    response.body()?.let { body ->
                        val str = body.string()
                        logger.info("${tabell.name}.json: $str")
                        return Gson().fromJson(str)
                    } ?: throw RuntimeException("Forventet respons med body, men mottok ingenting")
                }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun hentAntallPotensieltOppdaterteTilstander(): Int =
        try {
            restClient.newCall(
                buildRequest("$baseUrl/api/migrering/registrering-tilstand/antall-potensielt-oppdaterte")
            )
                .execute().use { response ->
                    response.body()?.let { body ->
                        val bodyString = body.string()
                        logger.info("Antall tilstander: $bodyString")
                        Gson().fromJson<Map<String, Int>>(bodyString)
                    }
                }?.get("antall") ?: throw RuntimeException("Forventet respons med body, men mottok ingenting")
        } catch (e: IOException) {
            0
        }

    override fun hentOppdaterteRegistreringStatuser(trengerOppdatering: List<RegistreringTilstand>): List<Map<String, Any>> {
        val map = trengerOppdatering.associate { it.id to it.status }

        return try {
            restClient.newCall(
                requestBuilder("$baseUrl/api/migrering/registrering-tilstand/hent-oppdaterte-statuser")
                    .post(RequestBody.create(MediaType.parse("application/json"), Gson().toJson(map)))
                    .build()
            ).execute().use { response ->
                response.body()?.let { body ->
                    val bodyString = body.string()
                    val oppdaterte_tilstander: List<Map<String, Any>> = Gson().fromJson(bodyString)
                    logger.info("Hentet ${oppdaterte_tilstander.size} oppdaterte tilstander")
                    return oppdaterte_tilstander
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
                .header("x-token", System.getenv("MIGRATION_TOKEN"))

        private val restClient = OkHttpClient.Builder()
            .readTimeout(240L, TimeUnit.SECONDS)
            .followRedirects(false)
            .build()

        inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, object: TypeToken<T>() {}.type)
    }
}