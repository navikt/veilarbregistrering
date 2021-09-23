package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.CacheConfig
import org.springframework.cache.annotation.Cacheable
import java.util.stream.Collectors

open class ArbeidsforholdGatewayImpl(private val aaregRestClient: AaregRestClient) : ArbeidsforholdGateway {
    @Cacheable(CacheConfig.HENT_ARBEIDSFORHOLD)
    override fun hentArbeidsforhold(fnr: Foedselsnummer): FlereArbeidsforhold =
        FlereArbeidsforhold(
            aaregRestClient.finnArbeidsforhold(fnr).map(ArbeidsforholdMapperV2::map)
        )

}