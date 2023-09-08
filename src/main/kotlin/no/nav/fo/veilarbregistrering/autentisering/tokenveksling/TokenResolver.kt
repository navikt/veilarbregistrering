package no.nav.fo.veilarbregistrering.autentisering.tokenveksling

import no.nav.common.auth.context.AuthContextHolder

class TokenResolver(private val authContextHolder: AuthContextHolder, private val tokenIssuers: TokenIssuers) {

    fun token(): String = authContextHolder.requireContext().idToken.serialize()

    private fun hentIssuer(): String = authContextHolder.requireIdTokenClaims().issuer
    fun erAzureAdToken(): Boolean = hentIssuer() === tokenIssuers.aadIssuer

    private fun erSystemTilSystemToken(): Boolean =
        authContextHolder.subject == authContextHolder.getStringClaim(authContextHolder.idTokenClaims.get(), "oid")

    fun erAzureAdOboToken(): Boolean = erAzureAdToken() && !erSystemTilSystemToken()

    fun erAzureAdSystemTilSystemToken(): Boolean = erAzureAdToken() && erSystemTilSystemToken()

    fun erTokenXToken(): Boolean = hentIssuer() === tokenIssuers.tokenXIssuer

    fun erIdPortenToken(): Boolean = hentIssuer() === tokenIssuers.idportenIssuer
}
