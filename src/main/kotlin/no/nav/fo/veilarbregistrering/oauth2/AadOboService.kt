package no.nav.fo.veilarbregistrering.oauth2

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.erAADToken
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.security.token.support.client.core.ClientAuthenticationProperties
import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.OAuth2GrantType
import no.nav.security.token.support.client.core.context.JwtBearerTokenResolver
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.core.oauth2.OnBehalfOfTokenClient
import java.net.URI
import java.util.*


class AadOboService(private val authContextHolder: AuthContextHolder) {
    private val tokenEndpointUrl: URI = URI.create(requireProperty("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT"))
    private val discoveryUrl: URI = URI.create(requireProperty("AZURE_APP_WELL_KNOWN_URL"))
    private val onBehalfOfTokenClient = OnBehalfOfTokenClient(DefaultOAuth2HttpClient())
    private val tokenResolver = TokenResolver(authContextHolder)
    private val accessTokenService =
            OAuth2AccessTokenService(tokenResolver, onBehalfOfTokenClient, null, null)

    fun getAccessToken(api: DownstreamApi): String {

        val clientProperties = ClientProperties(
            tokenEndpointUrl,
            discoveryUrl,
            OAuth2GrantType.JWT_BEARER,
            listOf("api://${api.cluster}.${api.namespace}.${api.appName}/.default"),
            ClientAuthenticationProperties(
                requireProperty("AZURE_APP_CLIENT_ID"),
                ClientAuthenticationMethod.CLIENT_SECRET_POST,
                requireProperty("AZURE_APP_CLIENT_SECRET"),
                null
            ),
            null,
            ClientProperties.TokenExchangeProperties(
                requireProperty("AZURE_APP_CLIENT_ID"),
                null
            )
        )
        return accessTokenService.getAccessToken(clientProperties).accessToken ?: throw IllegalStateException("Did not get access token")
    }

    fun erAzureAdToken(): Boolean {
        return authContextHolder.erAADToken()
    }
}

class TokenResolver(private val authContextHolder: AuthContextHolder) : JwtBearerTokenResolver {

    override fun token(): Optional<String> {
        return Optional.of(authContextHolder.requireContext().idToken.serialize())

    }
}

data class DownstreamApi(val cluster: String, val namespace: String, val appName: String)
