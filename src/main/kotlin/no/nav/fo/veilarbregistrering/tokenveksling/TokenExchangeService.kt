package no.nav.fo.veilarbregistrering.tokenveksling

import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.builder.TokenXTokenClientBuilder
import no.nav.fo.veilarbregistrering.log.logger

class TokenExchangeService(private val tokenResolver: TokenResolver) {

    private val tokendingsClient = TokenXTokenClientBuilder.builder()
        .withNaisDefaults()
        .buildOnBehalfOfTokenClient()

    private val aadOnBehalfOfTokenClient = AzureAdTokenClientBuilder.builder()
        .withNaisDefaults()
        .buildOnBehalfOfTokenClient()

    fun tokenSkalVeksles(): Boolean {
        return tokenResolver.erTokenXToken() || tokenResolver.erAzureAdToken()
    }

    fun exchangeToken(api: DownstreamApi): String {
        val opprinneligToken = tokenResolver.token()
        return when {
            tokenResolver.erTokenXToken() -> exchangeTokenXToken(api, opprinneligToken)
            tokenResolver.erAzureAdToken() -> exchangeAadOboToken(api, opprinneligToken)
            else -> throw IllegalStateException("Prøver å veksle et token som ikke er AAD OBO eller TokenX")
        }
    }

    private fun exchangeTokenXToken(api: DownstreamApi, opprinneligToken: String): String {
        logger.info("Veksler TokenX-token mot ${api.appName}")
        return tokendingsClient.exchangeOnBehalfOfToken(
            "${api.cluster}:${api.namespace}:${api.appName}",
            opprinneligToken
        )
    }

    private fun exchangeAadOboToken(api: DownstreamApi, opprinneligToken: String): String {
        logger.info("Veksler Azure AD-token mot ${api.appName}")
        return aadOnBehalfOfTokenClient.exchangeOnBehalfOfToken(
            "api://${api.cluster}.${api.namespace}.${api.appName}/.default",
            opprinneligToken
        )
    }
}