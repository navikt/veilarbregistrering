package no.nav.fo.veilarbregistrering.orgenhet.adapter

import no.nav.fo.veilarbregistrering.config.CacheConfig
import no.nav.fo.veilarbregistrering.enhet.Kommune
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import org.springframework.cache.annotation.Cacheable

internal open class Norg2GatewayImpl(private val norg2RestClient: Norg2RestClient) : Norg2Gateway {

    override fun hentEnhetFor(kommune: Kommune): Enhetnr? =
        norg2RestClient.hentEnhetFor(kommune)
            .firstOrNull { "Aktiv" == it.status }
            ?.enhetNr
            ?.let { Enhetnr(it) }

    @Cacheable(CacheConfig.HENT_ALLE_ENHETER_V2)
    override fun hentAlleEnheter(): Map<Enhetnr, NavEnhet> {
        val rsEnhets = norg2RestClient.hentAlleEnheter()
        return rsEnhets
            .associate { Enhetnr(it.enhetNr) to NavEnhet(it.enhetNr, it.navn) }
    }
}