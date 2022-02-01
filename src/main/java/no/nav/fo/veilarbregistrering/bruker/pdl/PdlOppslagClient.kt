package no.nav.fo.veilarbregistrering.bruker.pdl

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.utils.UrlUtils
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.feil.BrukerIkkeFunnetException
import no.nav.fo.veilarbregistrering.bruker.feil.PdlException
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.*
import no.nav.fo.veilarbregistrering.log.loggerFor
import okhttp3.Headers
import okhttp3.Request
import java.io.IOException
import java.nio.charset.StandardCharsets

open class PdlOppslagClient(
    private val baseUrl: String,
    private val tokenProvider: () -> String = { "default" }
) {

    private val mapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun hentIdenter(fnr: Foedselsnummer): PdlIdenter {
        val request = PdlHentIdenterRequest(hentIdenterQuery(), HentIdenterVariables(fnr.stringValue()))
        val json = hentIdenterRequest(fnr.stringValue(), request)
        val response = mapAndValidateResponse<PdlHentIdenterResponse>(json)
        return response.data.hentIdenter
    }

    fun hentIdenter(aktorId: AktorId): PdlIdenter {
        val request = PdlHentIdenterRequest(hentIdenterQuery(), HentIdenterVariables(aktorId.aktorId))
        val json = hentIdenterRequest(aktorId.aktorId, request)
        val response = mapAndValidateResponse<PdlHentIdenterResponse>(json)
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
            HentGeografiskTilknytningVariables(aktorId.aktorId)
        )
        val json = hentGeografiskTilknytningRequest(aktorId.aktorId, request)
        val resp = mapAndValidateResponse<PdlHentGeografiskTilknytningResponse>(json)
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
        val request = PdlHentPersonRequest(hentPersonQuery(), HentPersonVariables(aktorId.aktorId, false))
        val json = hentPersonRequest(aktorId.aktorId, request)
        val resp = mapAndValidateResponse<PdlHentPersonResponse>(json)
        return resp.data.hentPerson
    }

    open fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest): String {
        return hentFraPdl(pdlHentPersonRequest, ekstraHeaders = mapOf(
            NAV_PERSONIDENT_HEADER to fnr,
            TEMA_HEADER to OPPFOLGING_TEMA_HEADERVERDI,
        ))
    }

    private fun lagAuthHeaders(): Map<String, String> {
        val aadToken = tokenProvider()
        return mapOf(
            "Authorization" to "Bearer $aadToken",
        )
    }

    private fun hentIdenterQuery() = hentRessursfil("pdl/hentIdenter.graphql")
    private fun hentPersonQuery() = hentRessursfil("pdl/hentPerson.graphql")
    private fun hentGeografisktilknytningQuery() = hentRessursfil("pdl/hentGeografiskTilknytning.graphql")

    private fun hentRessursfil(sti: String): String {
        val classLoader = PdlOppslagClient::class.java.classLoader
        return try {
            classLoader.getResourceAsStream(sti)?.use { resourceStream ->
                String(
                    resourceStream.readAllBytes(),
                    StandardCharsets.UTF_8
                ).replace("[\n\r]".toRegex(), "")
            } ?: throw RuntimeException("File input stream var null")
        } catch (e: IOException) {
            throw RuntimeException("Integrasjon mot PDL ble ikke gjennomført pga. feil ved lesing av query", e)
        }
    }

    private inline fun <reified T> mapAndValidateResponse(response: String): T {
        return try {
            mapper.readValue(response)
        } catch (e: JsonProcessingException) {
            val pdlError = mapPdlErrorResponse(response)
            val errorCodes = pdlError.errors.mapNotNull { it.extensions?.code }
            if ("not_found" in errorCodes) {
                throw BrukerIkkeFunnetException("Fant ikke person i PDL")
            } else {
                throw PdlException("Integrasjon mot PDL feilet", pdlError.errors)
            }
        }
    }

    private fun mapPdlErrorResponse(response: String) : PdlErrorResponse {
        return try {
            mapper.readValue(response)
        } catch(e: JsonProcessingException) {
            throw PdlException("Integrasjon mot PDL feilet", emptyList())
        }
    }

    companion object {
        private val logger = loggerFor<PdlOppslagClient>()
        private const val NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token"
        private const val NAV_PERSONIDENT_HEADER = "Nav-Personident"
        private const val TEMA_HEADER = "Tema"
        private const val OPPFOLGING_TEMA_HEADERVERDI = "OPP"
    }
}
