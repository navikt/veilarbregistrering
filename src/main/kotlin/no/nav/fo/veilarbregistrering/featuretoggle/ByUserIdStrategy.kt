package no.nav.fo.veilarbregistrering.featuretoggle

import no.finn.unleash.strategy.Strategy
import no.nav.common.auth.context.AuthContextHolder
import org.slf4j.LoggerFactory

class ByUserIdStrategy(private val authContextHolder: AuthContextHolder) : Strategy {

    override fun getName(): String = "byUserId"

    override fun isEnabled(parameters: MutableMap<String, String>): Boolean {
        val navIdentIKontekst: String = authContextHolder.navIdent.get().get()
        val result = parameters["user"]
            ?.split(',')
            ?.any { navIdentIKontekst == it }
            ?: false
        LOG.info("ByUserId strategi er enabled: $result")
        return result
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }
}