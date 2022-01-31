package no.nav.fo.veilarbregistrering.orgenhet.adapter

import no.nav.fo.veilarbregistrering.config.CacheConfig
import no.nav.fo.veilarbregistrering.enhet.Kommune
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import org.springframework.cache.annotation.Cacheable
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

internal open class Norg2GatewayImpl(private val norg2RestClient: Norg2RestClient) : Norg2Gateway {

    override fun hentEnhetFor(kommune: Kommune): Optional<Enhetnr> {
        val listeMedRsNavKontorDtos = norg2RestClient.hentEnhetFor(kommune)
        return listeMedRsNavKontorDtos.stream()
            .filter { rsNavKontorDtos: RsNavKontorDto -> "Aktiv" == rsNavKontorDtos.status }
            .findFirst()
            .map { rsNavKontorDtos: RsNavKontorDto -> Enhetnr(rsNavKontorDtos.enhetNr) }
    }

    @Cacheable(CacheConfig.HENT_ALLE_ENHETER_V2)
    override fun hentAlleEnheter(): Map<Enhetnr, NavEnhet> {
        val rsEnhets = norg2RestClient.hentAlleEnheter()
        return rsEnhets.stream()
            .map { rs: RsEnhet -> NavEnhet(rs.enhetNr, rs.navn) }
            .collect(Collectors.toMap(
                Function { navEnhet: NavEnhet -> Enhetnr(navEnhet.id) },
                Function { navEnhet: NavEnhet -> navEnhet })
            )
    }
}