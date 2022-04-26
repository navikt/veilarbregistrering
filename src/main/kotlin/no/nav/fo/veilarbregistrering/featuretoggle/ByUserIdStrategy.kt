package no.nav.fo.veilarbregistrering.featuretoggle

import no.finn.unleash.strategy.Strategy
import no.nav.common.auth.context.AuthContextHolder

class ByUserIdStrategy(private val authContextHolder: AuthContextHolder) : Strategy {

    override fun getName(): String = "byUserId"

    override fun isEnabled(parameters: MutableMap<String, String>): Boolean {
        val navIdentIKontekst: String = authContextHolder.navIdent.get().get()
        return parameters["user"]
            ?.split(',')
            ?.any { navIdentIKontekst == it }
            ?: false
    }
}