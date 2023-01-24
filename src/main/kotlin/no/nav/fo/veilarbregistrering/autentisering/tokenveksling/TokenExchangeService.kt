package no.nav.fo.veilarbregistrering.autentisering.tokenveksling

import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.builder.TokenXTokenClientBuilder
import no.nav.fo.veilarbregistrering.log.logger

class TokenExchangeService(private val tokenResolver: TokenResolver) {

    fun tokenSkalVeksles(): Boolean {
        return tokenResolver.erTokenXToken() || tokenResolver.erAzureAdToken()
    }

    fun exchangeToken(api: DownstreamApi): String {
        val opprinneligToken = tokenResolver.token()
        return when {
            tokenResolver.erIdPortenToken() -> exchangeTokenXToken(api, opprinneligToken)
            tokenResolver.erTokenXToken() -> exchangeTokenXToken(api, opprinneligToken)
            tokenResolver.erAzureAdOboToken() -> exchangeAadOboToken(api, opprinneligToken)
            tokenResolver.erAzureAdSystemTilSystemToken() -> createAadMachineToMachineToken(api)
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

    private val tokendingsClient = TokenXTokenClientBuilder.builder()
        .withNaisDefaults()
        .buildOnBehalfOfTokenClient()

    private fun exchangeAadOboToken(api: DownstreamApi, opprinneligToken: String): String {
        logger.info("Veksler Azure AD OBO-token mot ${api.appName}")
        return aadOnBehalfOfTokenClient.exchangeOnBehalfOfToken(
            "api://${api.cluster}.${api.namespace}.${api.appName}/.default",
            opprinneligToken
        )
    }

    private val aadOnBehalfOfTokenClient = AzureAdTokenClientBuilder.builder()
        .withNaisDefaults()
        .buildOnBehalfOfTokenClient()

    private fun createAadMachineToMachineToken(api: DownstreamApi): String {
        logger.info("Lager nytt Azure AD M2M-token mot ${api.appName}")
        return aadMachineToMachineTokenClient.createMachineToMachineToken(
            "api://${api.cluster}.${api.namespace}.${api.appName}/.default"
        )
    }

    private val aadMachineToMachineTokenClient = AzureAdTokenClientBuilder.builder()
        .withNaisDefaults()
        .buildMachineToMachineTokenClient()
}