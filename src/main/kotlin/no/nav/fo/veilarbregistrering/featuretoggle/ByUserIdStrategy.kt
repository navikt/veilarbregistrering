package no.nav.fo.veilarbregistrering.featuretoggle

import no.finn.unleash.strategy.Strategy
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService
import org.slf4j.LoggerFactory

class ByUserIdStrategy(private val authContextHolder: AuthContextHolder) : Strategy {

    override fun getName(): String = "byUserId"

    override fun isEnabled(parameters: MutableMap<String, String>): Boolean {

        LOG.info("ByUserIdStrategy.isEnabled?")

        val navIdentIKontekst: String = authContextHolder.navIdent.get().get()

        LOG.info("Nav Ident i kontekst: {}", navIdentIKontekst)

        return parameters["user"]
            ?.split(',')
            ?.any { navIdentIKontekst == it }
            ?: false
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ByUserIdStrategy::class.java)
    }
}