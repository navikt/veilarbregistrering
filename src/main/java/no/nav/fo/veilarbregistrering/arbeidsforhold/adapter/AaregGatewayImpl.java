package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.AaregGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import java.util.List;
import java.util.stream.Collectors;

public class AaregGatewayImpl implements AaregGateway {

    private final AaregRestClient aaregRestClient;

    public AaregGatewayImpl(AaregRestClient aaregRestClient) {
        this.aaregRestClient = aaregRestClient;
    }

    @Override
    public FlereArbeidsforhold hentArbeidsforhold(Foedselsnummer fnr) {
        List<ArbeidsforholdDto> arbeidsforholdDtos = aaregRestClient.finnArbeidsforhold(fnr);

        return FlereArbeidsforhold.of(arbeidsforholdDtos.stream()
                .map(ArbeidsforholdMapperV2::map)
                .collect(Collectors.toList()));
    }
}
