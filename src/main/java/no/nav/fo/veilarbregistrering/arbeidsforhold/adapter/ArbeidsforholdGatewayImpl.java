package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.stream.Collectors;

import static no.nav.fo.veilarbregistrering.config.CacheConfig.HENT_ARBEIDSFORHOLD;

public class ArbeidsforholdGatewayImpl implements ArbeidsforholdGateway {

    private final AaregRestClient aaregRestClient;

    public ArbeidsforholdGatewayImpl(AaregRestClient aaregRestClient) {
        this.aaregRestClient = aaregRestClient;
    }

    @Override
    @Cacheable(HENT_ARBEIDSFORHOLD)
    public FlereArbeidsforhold hentArbeidsforhold(Foedselsnummer fnr) {
        List<ArbeidsforholdDto> arbeidsforholdDtos = aaregRestClient.finnArbeidsforhold(fnr);
        return new FlereArbeidsforhold(arbeidsforholdDtos.stream()
                .map(ArbeidsforholdMapperV2::map)
                .collect(Collectors.toList()));
    }
}