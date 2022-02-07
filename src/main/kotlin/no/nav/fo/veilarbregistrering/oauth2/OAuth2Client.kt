package no.nav.fo.veilarbregistrering.oauth2


import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.http.defaultHttpClient
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.security.token.support.client.core.http.OAuth2HttpClient
import no.nav.security.token.support.client.core.http.OAuth2HttpRequest
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import okhttp3.FormBody
import okhttp3.Request

class DefaultOAuth2HttpClient : OAuth2HttpClient {

    override fun post(oAuth2HttpRequest: OAuth2HttpRequest): OAuth2AccessTokenResponse {
        val formBody = FormBody.Builder().also { builder ->
            oAuth2HttpRequest.formParameters.forEach { (key, value) ->
                builder.add(key, value)
            }
        }.build()

        try {
            val response = defaultHttpClient().newCall(
                Request.Builder()
                    .url(oAuth2HttpRequest.tokenEndpointUrl.toString())
                    .post(formBody)
                    .build()
            ).execute()
            return response.body()?.string()?.let { objectMapper.readValue(it) } ?: throw Exception("Mangler responsbody")
        } catch (ex: Exception) {
            logger.error("Feil i oauth kall ${oAuth2HttpRequest.tokenEndpointUrl} ${oAuth2HttpRequest.formParameters}")
            throw ex
        }
    }
}
