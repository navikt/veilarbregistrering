package no.nav.fo.veilarbregistrering.bruker.pdl

import com.google.gson.*
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.feil.BrukerIkkeFunnetException
import no.nav.fo.veilarbregistrering.bruker.feil.PdlException
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.*
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.util.*

open class PdlOppslagClient(
    private val baseUrl: String,
    private val systemUserTokenProvider: SystemUserTokenProvider,
    tokenProvider: () -> String = { "" }
) {

    private val gson = GsonBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer()).create()

    fun hentIdenter(fnr: Foedselsnummer): PdlIdenter {
        val request = PdlHentIdenterRequest(hentIdenterQuery(), HentIdenterVariables(fnr.stringValue()))
        val json = hentIdenterRequest(fnr.stringValue(), request)
        val response = gson.fromJson(json, PdlHentIdenterResponse::class.java)
        validateResponse(response)
        return response.data.hentIdenter
    }

    fun hentIdenter(aktorId: AktorId): PdlIdenter {
        val request = PdlHentIdenterRequest(hentIdenterQuery(), HentIdenterVariables(aktorId.asString()))
        val json = hentIdenterRequest(aktorId.asString(), request)
        val response = gson.fromJson(json, PdlHentIdenterResponse::class.java)
        validateResponse(response)
        return response.data.hentIdenter
    }

    open fun hentIdenterRequest(personident: String, pdlHentIdenterRequest: PdlHentIdenterRequest): String {
        return hentFraPdl(pdlHentIdenterRequest, ekstraHeaders = mapOf(
            NAV_PERSONIDENT_HEADER to personident,
        ))
    }

    private fun hentFraPdl(
        graphqlRequest: Any,
        ekstraHeaders: Map<String, String>
    ): String {
        val requestBody = RestUtils.toJsonRequestBody(graphqlRequest)
        val authHeaders = lagAuthHeaders()
        val request = Request.Builder()
            .url(UrlUtils.joinPaths(baseUrl, "/graphql"))
            .headers(Headers.of(authHeaders + ekstraHeaders))
            .method("POST", requestBody)
            .build()
        try {
            RestClient.baseClient().newCall(request).execute()
                .use { response -> return RestUtils.getBodyStr(response).orElseThrow { RuntimeException() } }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun hentGeografiskTilknytning(aktorId: AktorId): PdlGeografiskTilknytning {
        val request = PdlHentGeografiskTilknytningRequest(
            hentGeografisktilknytningQuery(),
            HentGeografiskTilknytningVariables(aktorId.asString())
        )
        val json = hentGeografiskTilknytningRequest(aktorId.asString(), request)
        val resp = gson.fromJson(json, PdlHentGeografiskTilknytningResponse::class.java)
        validateResponse(resp)
        return resp.data.hentGeografiskTilknytning
    }

    open fun hentGeografiskTilknytningRequest(
        fnr: String,
        pdlHentGeografiskTilknytningRequest: PdlHentGeografiskTilknytningRequest
    ): String {
        return hentFraPdl(pdlHentGeografiskTilknytningRequest, ekstraHeaders = mapOf(
            NAV_PERSONIDENT_HEADER to fnr,
            TEMA_HEADER to OPPFOLGING_TEMA_HEADERVERDI,
        ))
    }

    fun hentPerson(aktorId: AktorId): PdlPerson {
        val request = PdlHentPersonRequest(hentPersonQuery(), HentPersonVariables(aktorId.asString(), false))
        val json = hentPersonRequest(aktorId.asString(), request)
        val resp = gson.fromJson(json, PdlHentPersonResponse::class.java)
        validateResponse(resp)
        return resp.data.hentPerson
    }

    open fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest): String {
        return hentFraPdl(pdlHentPersonRequest, ekstraHeaders = mapOf(
            NAV_PERSONIDENT_HEADER to fnr,
            TEMA_HEADER to OPPFOLGING_TEMA_HEADERVERDI,
        ))
    }

    private fun lagAuthHeaders(): Map<String, String> {
        val token = systemUserTokenProvider.systemUserToken
        val authHeaders = mapOf(
            "Authorization" to "Bearer $token",
            NAV_CONSUMER_TOKEN_HEADER to "Bearer $token",
        )
        return authHeaders
    }

    private fun hentIdenterQuery() = hentRessursfil("pdl/hentIdenter.graphql")
    private fun hentPersonQuery() = hentRessursfil("pdl/hentPerson.graphql")
    private fun hentGeografisktilknytningQuery() = hentRessursfil("pdl/hentGeografiskTilknytning.graphql")

    private fun hentRessursfil(sti: String): String {
        val classLoader = PdlOppslagClient::class.java.classLoader
        try {
            classLoader.getResourceAsStream(sti).use { resourceStream ->
                return String(
                    resourceStream.readAllBytes(),
                    StandardCharsets.UTF_8
                ).replace("[\n\r]".toRegex(), "")
            }
        } catch (e: IOException) {
            throw RuntimeException("Integrasjon mot PDL ble ikke gjennomført pga. feil ved lesing av query", e)
        }
    }

    private fun validateResponse(response: PdlResponse) {
        if (response.errors != null && response.errors!!.isNotEmpty()) {
            if (response.errors!!.any { pdlError -> pdlError.extensions?.code == "not_found" }) {
                throw BrukerIkkeFunnetException("Fant ikke person i PDL")
            }
            throw PdlException("Integrasjon mot PDL feilet", response.errors!!)
        }
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
        private const val NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token"
        private const val NAV_PERSONIDENT_HEADER = "Nav-Personident"
        private const val TEMA_HEADER = "Tema"
        private const val OPPFOLGING_TEMA_HEADERVERDI = "OPP"
    }
}
