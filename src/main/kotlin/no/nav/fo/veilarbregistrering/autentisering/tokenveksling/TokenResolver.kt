package no.nav.fo.veilarbregistrering.autentisering.tokenveksling

import no.nav.common.auth.context.AuthContextHolder

class TokenResolver(private val authContextHolder: AuthContextHolder) {

    fun token(): String {
        return authContextHolder.requireContext().idToken.serialize()
    }

    fun erAzureAdToken(): Boolean {
        return authContextHolder.erAADToken()
    }

    fun erAzureAdOboToken(): Boolean {
        return authContextHolder.erAADToken() && !authContextHolder.erSystemTilSystemToken()
    }

    fun erAzureAdSystemTilSystemToken(): Boolean {
        return authContextHolder.erAADToken() && authContextHolder.erSystemTilSystemToken()
    }

    fun erTokenXToken(): Boolean {
        return authContextHolder.erTokenXToken()
    }

    fun erIdPortenToken(): Boolean {
        return authContextHolder.erIdPortenToken()
    }
}

fun AuthContextHolder.erAADToken(): Boolean = hentIssuer().contains("login.microsoftonline.com")
private fun AuthContextHolder.erSystemTilSystemToken(): Boolean = this.subject == this.getStringClaim(this.idTokenClaims.get(), "oid")
private fun AuthContextHolder.erTokenXToken(): Boolean = hentIssuer().contains("tokenx")
private fun AuthContextHolder.erIdPortenToken(): Boolean = hentIssuer().contains("difi.no")
private fun AuthContextHolder.hentIssuer(): String = this.requireIdTokenClaims().issuer
