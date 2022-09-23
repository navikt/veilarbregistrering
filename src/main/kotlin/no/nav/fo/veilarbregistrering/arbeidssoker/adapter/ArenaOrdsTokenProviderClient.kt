package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.http.defaultHttpClient
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.springframework.http.HttpHeaders
import java.io.IOException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class ArenaOrdsTokenProviderClient(
    private val arenaOrdsUrl: String,
    private val proxyTokenProvider: () -> String
) {

    private var tokenCache: TokenCache? = null
    val token: String
        get() {
            if (tokenIsSoonExpired()) {
                tokenCache = TokenCache(getRefreshedToken())
            }
            return tokenCache!!.ordsToken.accessToken
        }

    private fun getRefreshedToken(): OrdsToken {
        val request = if (isOnPrem()) buildRequest() else buildRequestGcp()
        return try {
            defaultHttpClient().newCall(request).execute().use { response ->
                when {
                    response.isSuccessful -> {
                        response.body()?.string()
                            ?.let { bodyString -> objectMapper.readValue<OrdsToken>(bodyString) }
                            ?: throw IOException("Token response body was null")
                    }
                    else -> throw IOException("Unexpected response code (${response.code()}) from ords token refresh")
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun buildRequest() = Request.Builder()
        .url(HttpUrl.parse(arenaOrdsUrl)!!.newBuilder().addPathSegments("arena/api/oauth/token").build())
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
        .header(HttpHeaders.AUTHORIZATION, basicAuth)
        .post(
            RequestBody.create(
                MediaType.get("application/x-www-form-urlencoded"),
                "grant_type=client_credentials"
            )
        )
        .build()


    private fun buildRequestGcp() = Request.Builder()
        .url(HttpUrl.parse(arenaOrdsUrl)!!.newBuilder().addPathSegments("arena/token").build())
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
        .header(HttpHeaders.AUTHORIZATION, "Bearer ${proxyTokenProvider()}")
        .header("Downstream-Authorization", basicAuth)
        .post(
            RequestBody.create(
                MediaType.get("application/x-www-form-urlencoded"),
                "grant_type=client_credentials"
            )
        )
        .build()

    private fun tokenIsSoonExpired(): Boolean {
        return tokenCache == null || timeToRefresh().isBefore(LocalDateTime.now())
    }

    private fun timeToRefresh(): LocalDateTime {
        return tokenCache!!.time.plus(
            (
                    tokenCache!!.ordsToken.expiresIn - MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH).toLong(),
            ChronoUnit.SECONDS
        )
    }

    private class TokenCache(val ordsToken: OrdsToken) {
        val time: LocalDateTime = LocalDateTime.now()

    }

    data class OrdsToken(
        @JsonAlias("access_token")
        val accessToken: String,

        @JsonAlias("token_type")
        val tokenType: String,

        @JsonAlias("expires_in")
        val expiresIn: Int = 0
    )

    companion object {
        private const val MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH = 60

        private const val ARENA_ORDS_CLIENT_ID_PROPERTY = "ARENA_ORDS_CLIENT_ID"
        private const val ARENA_ORDS_CLIENT_SECRET_PROPERTY = "ARENA_ORDS_CLIENT_SECRET"
        private val username = requireProperty(ARENA_ORDS_CLIENT_ID_PROPERTY)
        private val password = requireProperty(ARENA_ORDS_CLIENT_SECRET_PROPERTY)
        private val basicAuth = "Basic ${Base64.getEncoder().encodeToString("$username:$password".toByteArray())}"
    }
}